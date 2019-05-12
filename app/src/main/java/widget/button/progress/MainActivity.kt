package widget.button.progress

import android.animation.ValueAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        valueAnimator()
    }

    private fun valueAnimator(){
        val valueAnimator = ValueAnimator.ofInt(100)
        valueAnimator.duration = 2000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.setInterpolator(LinearInterpolator())
        valueAnimator.addUpdateListener { animation ->
            Log.e("tag", animation?.animatedValue.toString())
        }
        valueAnimator.start()

    }

    fun onClick(view: View) {
        if (view == btnLoading) {
            Toast.makeText(this,"loading",Toast.LENGTH_LONG).show()
            btnLoading.showLoading()
        }else if (view == btnStop) {
            btnLoading.stopLoading()
        }
    }
}
