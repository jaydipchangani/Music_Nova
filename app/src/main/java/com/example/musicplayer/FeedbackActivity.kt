package com.example.musicplayer

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        setContentView(R.layout.activity_feedback)
        supportActionBar?.title = "Feedback"

        val feedback : TextInputEditText = findViewById(R.id.feedbackMsgFA)
        val email : TextInputEditText = findViewById(R.id.emailFA)
        val topic : TextInputEditText = findViewById(R.id.topicFA)
        val sendBtn : Button = findViewById(R.id.sendBtnFA)

        sendBtn.setOnClickListener {
            val feedbackMsg = feedback.text.toString() + "\n" + email.text.toString()
            val subject = topic.text.toString()
            val userName = "divympatel21@gnu.ac.in"
            val pass = "Divyp@tel2004"
            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (feedbackMsg.isNotEmpty() && subject.isNotEmpty() && cm.activeNetworkInfo?.isConnectedOrConnecting == true){
                Thread{
                    try {
                        val properties = Properties()
                        properties["mail.smtp.auth"] = "true"
                        properties["mail.smtp.starttls.enable"] = "true"
                        properties["mail.smtp.host"] = "smtp.gmail.com"
                        properties["mail.smtp.port"] = "587"
                        val session = Session.getInstance(properties, object : Authenticator(){
                            override fun getPasswordAuthentication(): PasswordAuthentication {
                                return PasswordAuthentication(userName, pass)
                            }
                        })
                        val mail = MimeMessage(session)
                        mail.subject = subject
                        mail.setText(feedbackMsg)
                        mail.setFrom(InternetAddress(userName))
                        mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userName))
                        Transport.send(mail)
                    }
                    catch (e:Exception){
                        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }.start()
                Toast.makeText(this, "Thanks for Feedback!!", Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}