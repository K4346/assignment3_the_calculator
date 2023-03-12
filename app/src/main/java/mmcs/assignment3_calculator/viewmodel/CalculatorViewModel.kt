package mmcs.assignment3_calculator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import net.objecthunter.exp4j.ExpressionBuilder

class CalculatorViewModel(application: Application) : AndroidViewModel(application), Calculator {

    override var display = MutableLiveData<String>()

    init {
        if (display.value == null) {
            display.value = ""
        }
    }

    override fun addDigit(dig: Int) {
        display.value = removeTrailingZeroes(display.value + dig.toString())

    }

    override fun addPoint() {
        display.value = addDotToEnd(display.value ?: "")
    }

    override fun addOperation(op: Operation) {
        if (display.value?.isEmpty() == true || display.value?.get(display.value!!.length - 1)
                ?.isDigit() == false
        ) return
        display.value = display.value + when (op) {
            Operation.MUL -> '*'
            Operation.DIV -> '/'
            Operation.ADD -> '+'
            Operation.SUB -> '-'
        }
    }

    override fun compute() {
        if (display.value?.isEmpty() == true || display.value?.get(display.value!!.length - 1)
                ?.isDigit() == false
        ) return
        display.value = ExpressionBuilder((display.value ?: "")).build().evaluate().toString()
//        display.value = calc(display.value ?: "").toString()
    }

    override fun clear() {
        display.value = ""
    }

    override fun reset() {
        display.value = ""
    }

    private fun removeTrailingZeroes(str: String): String {
        if (str.length < 2) return str
        if (str[str.lastIndex - 1] != '0') return str
        for (i in str.lastIndex - 1 downTo 0) {
            if (str[i] == '+' || str[i] == '-' || str[i] == '*' || str[i] == '/') return str.substring(
                0,
                str.lastIndex
            )
            if (str[i] == '.') return str
        }
        return str.substring(0, str.lastIndex)
    }

    private fun addDotToEnd(input: String): String {
        var output = input.trim()

        if (output.isBlank() || output.last().isDigit().not()) {
            return input
        }
        val lastNumIndex = output.lastIndexOfAny(charArrayOf('+', '-', '*', '/')) + 1
        val lastNum = output.substring(lastNumIndex)
        if (lastNum.contains('.')) {
            return input
        }
        output += "."
        return output
    }
}