package mmcs.assignment3_calculator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class CalculatorViewModel(application: Application) : AndroidViewModel(application), Calculator {

    override var display = MutableLiveData<String>()

    init {
        if (display.value == null) {
            display.value = ""
        }
    }

    override fun addDigit(dig: Int) {
        display.value = display.value + dig.toString()
    }

    override fun addPoint() {
        display.value = addDotToEnd(display.value?:"")
    }

    override fun addOperation(op: Operation) {
        if (display.value?.isEmpty() == true || display.value?.get(display.value!!.length - 1)
                ?.isDigit() == false
        ) return
        display.value = display.value + when (op) {
            Operation.MUL -> '×'
            Operation.DIV -> '÷'
            Operation.ADD -> '+'
            Operation.SUB -> '-'
        }
    }

    override fun compute() {
        if (display.value?.isEmpty() == true || display.value?.get(display.value!!.length - 1)
                ?.isDigit() == false
        ) return
        display.value = calc(display.value ?: "").toString()
    }

    override fun clear() {
        display.value = ""
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    fun calc(input: String): Double {
        // разбиение строки на элементы
        val regex = "([0-9\\.]+)|([+\\-×÷])".toRegex()
        val tokens = regex.findAll(input).map { it.value }.toList()

//        порядок
        val operatorPrecedence = mapOf("+" to 1, "-" to 1, "×" to 2, "÷" to 2)
        val operatorStack = mutableListOf<String>()
        val operandStack = mutableListOf<Double>()

        for (token in tokens) {
            when {
                token.matches("[0-9\\.]+".toRegex()) -> operandStack.add(token.toDouble())
                operatorPrecedence.containsKey(token) -> {
                    while (operatorStack.isNotEmpty() && operatorPrecedence[token]!! <= operatorPrecedence[operatorStack.last()]!!) {
                        val operator = operatorStack.removeLast()
                        val operand2 = operandStack.removeLast()
                        val operand1 = operandStack.removeLast()
                        val result = when (operator) {
                            "+" -> operand1 + operand2
                            "-" -> operand1 - operand2
                            "×" -> operand1 * operand2
                            "÷" -> operand1 / operand2
                            else -> throw IllegalArgumentException("Invalid operator: $operator")
                        }
                        operandStack.add(result)
                    }
                    operatorStack.add(token)
                }
                else -> throw IllegalArgumentException("Invalid token: $token")
            }
        }

        while (operatorStack.isNotEmpty()) {
            val operator = operatorStack.removeLast()
            val operand2 = operandStack.removeLast()
            val operand1 = operandStack.removeLast()
            val result = when (operator) {
                "+" -> operand1 + operand2
                "-" -> operand1 - operand2
                "×" -> operand1 * operand2
                "÷" -> operand1 / operand2
                else -> throw IllegalArgumentException("Invalid operator: $operator")
            }
            operandStack.add(result)
        }

        return operandStack.last()
    }

    fun addDotToEnd(input: String): String {
        var output = input.trim()

        if (output.isBlank() || output.last().isDigit().not()) {
            return input
        }
        val lastNumIndex = output.lastIndexOfAny(charArrayOf('+', '-', '×', '÷')) + 1
        val lastNum = output.substring(lastNumIndex)
        if (lastNum.contains('.')) {

            return input
        }
        output += "."
        return output
    }
}