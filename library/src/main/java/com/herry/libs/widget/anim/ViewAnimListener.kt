package com.herry.libs.widget.anim

class ViewAnimListener private constructor() {

    interface OnStart {
        fun onStart()
    }

    interface OnStop {
        fun onStop()
    }

    interface OnCancel {
        fun onCancel()
    }
/*
    interface OnUpdate<V extends View> {
        fun update(V view, float value)
    }
*/
}