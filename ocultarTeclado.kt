package com.example.elvermelo.template.Utilidades

import android.app.Activity
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.AppCompatEditText
import android.util.Log
//import android.support.v7.widget.CardView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*

/**
 *
 * HiddenKeyBoard.newInstance(window.decorView.rootView)
 o la vista que desea que capture el toque para ocultar el teclado
@Author Yesid Melo
 */
class HiddenKeyBoard {
    companion object {
        fun newInstance(view: View) {
            val hiddenKeyBoard = HiddenKeyBoard()
            hiddenKeyBoard.hiddenInInitialView(view)
            hiddenKeyBoard.hidden(view)
        }
    }


    private fun hidden(view: View) {
        when (view) {
            is ConstraintLayout -> {
                navigateConstraint(view, ::hidden)
            }
            is LinearLayout -> {
                navigateLinearLayout(view, ::hidden)
            }
            is RelativeLayout -> {
                navigateRelativeLayout(view, ::hidden)
            }
            is TableLayout -> {
                navigateTableLayout(view, ::hidden)
            }
            is TableRow -> {
                navigateTableRow(view, ::hidden)
            }
            is FrameLayout -> {
                navigateFrameLayout(view, ::hidden)
            }
//            is CardView -> {
//                navigateCardview(view, ::hidden)
//            }
            is EditText -> {
            }
            is AppCompatEditText -> {
            }
            is AutoCompleteTextView -> {
            }
            else -> {
                listennerHiden(view)
            }
        }
    }

    private fun listennerHiden(view: View) {
        view.setOnTouchListener { view, motionEvent ->
            val imm: InputMethodManager = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun hiddenInInitialView(view: View) {
        val imm: InputMethodManager = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun navigateConstraint(view: ConstraintLayout, hidden: (View) -> Unit) {
        listennerHiden(view)
        for(contador in 0 until view.childCount) {
            hidden(view.getChildAt(contador))
        }
    }
    private fun navigateFrameLayout(view: FrameLayout, hidden: (View) -> Unit) {
        listennerHiden(view)
        for(contador in 0 until view.childCount) {
            hidden(view.getChildAt(contador))
        }
    }

    private fun navigateLinearLayout(view: LinearLayout, hidden: (View) -> Unit) {
        listennerHiden(view)
        for(contador in 0 until view.childCount) {
            hidden(view.getChildAt(contador))
        }
    }

    private fun navigateRelativeLayout(view: RelativeLayout, hidden: (View) -> Unit) {
        listennerHiden(view)
        for(contador in 0 until view.childCount) {
            hidden(view.getChildAt(contador))
        }
    }

    private fun navigateTableLayout(view: TableLayout, hidden: (View) -> Unit) {
        listennerHiden(view)
        for(contador in 0 until view.childCount) {
            hidden(view.getChildAt(contador))
        }
    }

    private fun navigateTableRow(view: TableRow, hidden: (View) -> Unit) {
        listennerHiden(view)
        for(contador in 0 until view.childCount) {
            hidden(view.getChildAt(contador))
        }
    }

//    private fun navigateCardview(view: CardView, hidden: (View) -> Unit) {
//        listennerHiden(view)
//        for(contador in 0 until view.childCount) {
//            hidden(view.getChildAt(contador))
//        }
//    }
}