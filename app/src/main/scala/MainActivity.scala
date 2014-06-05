package com.chri.yourdeals.app

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.{Toast, Button}

class HelloActivity extends Activity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.fragment_main)

    val button = findViewById(R.id.button).asInstanceOf[Button]
    button.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) {
        showToast
      }
    })
  }

  def showToast {
    Toast.makeText(this, "You have clicked the button", Toast.LENGTH_LONG).show()
  }
}