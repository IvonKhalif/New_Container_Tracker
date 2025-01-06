package com.example.containertracker.utils

import android.text.method.PasswordTransformationMethod
import android.view.View

class Asterisk : PasswordTransformationMethod() {

    override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
        return PasswordCharSequence(source)
    }

    private class PasswordCharSequence(charSequence: CharSequence?) : CharSequence {
        private val mSource: CharSequence? = charSequence

        override val length: Int
            get() = mSource?.length ?: 0

        override fun get(index: Int): Char {
            return '*'
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return mSource!!.subSequence(startIndex, endIndex)
        }
    }

}