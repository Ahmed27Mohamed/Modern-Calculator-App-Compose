package com.a2004256_ahmedmohamed.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.a2004256_ahmedmohamed.calculatorapp.ui.theme.CalculatorTheme

val DeepNavy = Color(0xFF0F1221)
val GlowBlue = Color(0xFF4FACFE)
val GlowPink = Color(0xFFF72585)
val GlassWhite = Color.White.copy(alpha = 0.1f)
val TextWhite = Color.White
val TextGray = Color.White.copy(alpha = 0.6f)
val OperatorOrange = Color(0xFFFF9F43)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepNavy)
                ) {
                    BackgroundGlows()
                    CalculatorAppContent()
                }
            }
        }
    }
}

@Composable
fun BackgroundGlows() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        GlowPink.copy(alpha = 0.2f),
                        DeepNavy
                    ),
                    center = Offset(400f, 200f),
                    radius = 800f
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-50).dp)
                .blur(radius = 150.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(350.dp)
                .offset(x = 100.dp, y = 100.dp)
                .blur(radius = 150.dp)
                .background(GlowPink.copy(alpha = 0.2f), RoundedCornerShape(175.dp))
        )
    }
}

@Composable
fun CalculatorAppContent() {
    var displayValue by remember { mutableStateOf("0") }
    var previousValue by remember { mutableStateOf("") }
    var operation by remember { mutableStateOf<String?>(null) }
    var isNewInput by remember { mutableStateOf(true) }

    fun onNumberClick(num: String) {
        displayValue = if (isNewInput || displayValue == "0") num else displayValue + num
        isNewInput = false
    }

    fun calculateResult() {
        val num1 = previousValue.toDoubleOrNull() ?: return
        val num2 = displayValue.toDoubleOrNull() ?: return
        val result = when (operation) {
            "+" -> num1 + num2
            "−" -> num1 - num2
            "×" -> num1 * num2
            "÷" -> num1 / num2
            else -> return
        }
        displayValue = if (result % 1 == 0.0) result.toInt().toString() else result.toString()
        previousValue = ""
        operation = null
        isNewInput = true
    }

    fun onOperationClick(op: String) {
        if (operation != null) calculateResult()
        previousValue = displayValue
        operation = op
        isNewInput = true
    }

    fun onFunctionClick(func: String) {
        when (func) {
            "C" -> {
                displayValue = "0"
                previousValue = ""
                operation = null
            }
            "±" -> displayValue = (-displayValue.toDouble()).toString()
            "%" -> displayValue = (displayValue.toDouble() / 100).toString()
            "⌫" -> {
                if (displayValue.length > 1) displayValue = displayValue.dropLast(1)
                else displayValue = "0"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = if (previousValue.isNotEmpty() && operation != null) "$previousValue $operation" else "",
                style = TextStyle(color = TextGray, fontSize = 24.sp, fontWeight = FontWeight.Light),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = displayValue,
                style = TextStyle(
                    color = TextWhite,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(color = Color.White.copy(alpha = 0.3f), blurRadius = 10f)
                ),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .padding(20.dp)
        ) {
            KeypadGrid(
                onNumberClick = { num -> onNumberClick(num) },
                onOperationClick = { op -> onOperationClick(op) },
                onFunctionClick = { func -> onFunctionClick(func) },
                onEqualClick = { calculateResult() }
            )
        }
    }
}

@Composable
fun KeypadGrid(
    onNumberClick: (String) -> Unit,
    onOperationClick: (String) -> Unit,
    onFunctionClick: (String) -> Unit,
    onEqualClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val buttons = listOf(
            listOf("C", "±", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "−"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "⌫", "=")
        )

        for (row in buttons) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (btn in row) {
                    val isWide = btn == "0"
                    CalculatorGlassButton(
                        text = btn,
                        modifier = Modifier.weight(if (isWide) 2f else 1f),
                        shape = RoundedCornerShape(24.dp),
                        onClick = {
                            when {
                                btn in listOf("+", "−", "×", "÷") -> onOperationClick(btn)
                                btn == "=" -> onEqualClick()
                                btn in listOf("C", "±", "%", "⌫") -> onFunctionClick(btn)
                                else -> onNumberClick(btn)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorGlassButton(
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    onClick: () -> Unit
) {
    val isEqual = text == "="
    val isOperator = text in listOf("÷", "×", "−", "+")
    val isFunction = text in listOf("C", "±", "%", "⌫")

    val equalGradient = Brush.linearGradient(listOf(GlowBlue, GlowPink.copy(alpha = 0.8f)), tileMode = TileMode.Clamp)
    val operatorBackground = OperatorOrange.copy(alpha = 0.4f)
    val backgroundBrush = when {
        isEqual -> equalGradient
        isOperator -> Brush.verticalGradient(listOf(operatorBackground, operatorBackground.copy(alpha = 0.8f)))
        else -> Brush.verticalGradient(listOf(GlassWhite, GlassWhite.copy(alpha = 0.5f)))
    }

    val textColor = when {
        isEqual -> Color.White
        isOperator -> Color.White
        isFunction -> TextGray
        else -> Color.White
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(70.dp)
            .clip(shape)
            .background(backgroundBrush)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = textColor,
                fontSize = if (isFunction) 24.sp else 30.sp,
                fontWeight = if (isEqual) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun Preview1() {
    CalculatorTheme {
        Box(modifier = Modifier.fillMaxSize().background(DeepNavy)) {
            BackgroundGlows()
            CalculatorAppContent()
        }
    }
}