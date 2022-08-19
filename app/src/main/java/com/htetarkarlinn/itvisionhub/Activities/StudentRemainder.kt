package com.htetarkarlinn.itvisionhub.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.htetarkarlinn.itvisionhub.`object`.Mailer
import com.htetarkarlinn.itvisionhub.databinding.ActivityStudentRemainderBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StudentRemainder : AppCompatActivity() {
    lateinit var binding: ActivityStudentRemainderBinding
    var email=""
    var reminderLetter=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityStudentRemainderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title="Send Reminder"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        email=intent.getStringExtra("email").toString()
        //Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
        binding.studentMail.text="To : $email"
        binding.sendReminder.setOnClickListener {

            reminderLetter=binding.reminderTxt.text.toString()
            if (reminderLetter.equals("")){
                Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show()
            }else {
                binding.progressBar.visibility=View.VISIBLE
                binding.progressBar.progress
                binding.sendBtn.text="Sending"
                Mailer.sendMail(this, email, "Reminder from Admin", reminderLetter)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.sendBtn.text = "Send Reminder"
                        onBackPressed()
                    }, {
                        Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.sendBtn.text = "Send Reminder"
                    })
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}