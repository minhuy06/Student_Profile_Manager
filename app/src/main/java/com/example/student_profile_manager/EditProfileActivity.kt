package com.example.student_profile_manager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.student_profile_manager.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var originalStudent: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        originalStudent = intent.getSerializableExtra("STUDENT") as? Student
        originalStudent?.let {
            binding.edtName.setText(it.name)
            binding.edtClass.setText(it.className)
            binding.edtGpa.setText(it.gpa.toString())
        }

        binding.btnSave.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val className = binding.edtClass.text.toString().trim()
            val gpa = binding.edtGpa.text.toString().toDoubleOrNull()

            if (name.isEmpty() || className.isEmpty() || gpa == null || gpa !in 0.0..4.0) {
                Toast.makeText(this, "Vui lòng nhập dữ liệu hợp lệ (GPA 0.0-4.0)!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updated = originalStudent?.copy(name = name, className = className, gpa = gpa) ?: return@setOnClickListener
            val resIntent = Intent().apply {
                putExtra("UPDATED_STUDENT", updated)
            }
            setResult(Activity.RESULT_OK, resIntent)
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish() // Tự động trả về RESULT_CANCELED
        }
    }
}