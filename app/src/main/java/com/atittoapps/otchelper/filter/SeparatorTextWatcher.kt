package com.atittoapps.otchelper.filter

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.*


class SeparatorTextWatcher(val editText: EditText) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        try {
            val selection = editText.selectionStart
            editText.removeTextChangedListener(this)
            val value: String = editText.text.toString()
            val countOfFirst = value.substring(0, if(selection + 1 < value.length) selection + 1 else selection)?.count { it == ' ' } ?: 0
            if (value != null && value != "") {

                if (value.startsWith("0") && !value.startsWith("0.")) {
                    editText.setText("")
                }
                val str: String = editText.text.toString().replace(" ", "")
                val string = getDecimalFormattedString(str)
                if (value != "") editText.setText(string)
                val countOf = string?.substring(0, if(selection + 1 < string.length) selection + 1 else if(string.isNotEmpty()) string.length - 1 else 0)?.count { it == ' ' } ?: 0
                val adding = if(countOf != countOfFirst) countOf - countOfFirst else 0
                val finalSelection = if(selection + adding <= editText.text.length) selection + adding else string?.length ?: 0
                editText.setSelection(finalSelection)
            }
            editText.addTextChangedListener(this)
            return
        } catch (ex: Exception) {
            ex.printStackTrace()
            editText.addTextChangedListener(this)
        }
    }

    companion object {

        fun getDecimalFormattedString(value: String): String? {
            val lst = StringTokenizer(value, ".")
            var str1 = value
            var str2 = ""
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken()
                str2 = lst.nextToken()
            }
            var str3 = ""
            var i = 0
            var j = -1 + str1.length
            if (str1[-1 + str1.length] == '.') {
                j--
                str3 = "."
            }
            var k = j
            while (true) {
                if (k < 0) {
                    if (str2.isNotEmpty()) str3 = "$str3.$str2"
                    return str3
                }
                if (i == 3) {
                    str3 = " $str3"
                    i = 0
                }
                str3 = str1[k].toString() + str3
                i++
                k--
            }
        }
    }
}