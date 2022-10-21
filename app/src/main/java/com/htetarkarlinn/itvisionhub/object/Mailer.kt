package com.htetarkarlinn.itvisionhub.`object`

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import io.reactivex.Completable
import java.util.*
import javax.mail.Authenticator
import javax.mail.Message.RecipientType.TO
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


object Mailer {

    @SuppressLint("CheckResult")
    fun sendMail(context: Context,email: String, subject: String, message: String): Completable {
        return Completable.create { emitter ->

            //configure SMTP server
            val props = Properties()

            //Configuring properties for gmail
            //If you are not using gmail you may need to change the values
            props["mail.smtp.host"] = "smtp.gmail.com"
            /*props["mail.smtp.socketFactory.port"] = "587"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"*/
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.starttls.enable"]="true"
            props["mail.smtp.port"] = "587"//587


            //Creating a session
            val session = javax.mail.Session.getDefaultInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(Config.Email, Config.Password)
                }
            })

            try {

                //Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show()
                MimeMessage(session).let { mime ->
                    mime.setFrom(InternetAddress(Config.Email))
                    //Adding receiver
                    mime.addRecipient(TO, InternetAddress(email))
                    //Adding subject
                    mime.setSubject(subject)
                    //Adding message
                    mime.setText(message)
                    //send mail
                    Transport.send(mime)
                    Log.e("Display",mime.toString())
                }

            } catch (e: MessagingException) {
                //Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                emitter.onError(e)
            }
            //ending subscription
            emitter.onComplete()
        }
    }
}