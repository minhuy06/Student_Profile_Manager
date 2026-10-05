package com.example.student_profile_manager


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.student_profile_manager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "TAG_LIFECYCLE"

    // Khởi tạo thông tin cá nhân
    private var student = Student("2415053122117", "Võ Minh Huy", "24T1", "2415053122117@sv.ute.udn.vn", 3.8)

    // 1. Launcher: Chỉnh sửa hồ sơ (StartActivityForResult)
    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedStudent = result.data?.getSerializableExtra("UPDATED_STUDENT") as? Student
            updatedStudent?.let {
                student = it
                bindData(student)
                Toast.makeText(this, "Đã lưu thành công!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 2. Launcher: Đổi Avatar từ Gallery (GetContent)
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.imgAvatar.setImageURI(it)
            Toast.makeText(this, "Đã đổi avatar!", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Launcher Mở rộng 3: Chụp ảnh trực tiếp (TakePicturePreview)
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            binding.imgAvatar.setImageBitmap(it)
            Toast.makeText(this, "Đã cập nhật ảnh chụp!", Toast.LENGTH_SHORT).show()
        }
    }

    // 4. Launcher Mở rộng 2: Xin nhiều quyền lúc runtime (RequestMultiplePermissions)
    private val multiplePermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var allGranted = true
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            Log.d(TAG, "$permissionName cấp quyền: $isGranted")
            if (!isGranted) allGranted = false
        }
        if (allGranted) {
            Toast.makeText(this, "Đã cấp đủ quyền Camera & Micro!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Bạn đã từ chối một số quyền!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindData(student)

        // Các sự kiện nút bấm
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java).apply {
                putExtra("STUDENT", student)
            }
            editLauncher.launch(intent)
        }

        binding.btnChangeAvatar.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnTakePicture.setOnClickListener {
            takePhotoLauncher.launch(null) // Không cần tham số đầu vào
        }

        binding.btnRequestPermissions.setOnClickListener {
            multiplePermissionsLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            )
        }

        binding.btnCallHotline.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:0905123456")
            }
            startActivity(dialIntent)
        }

        // Mở rộng 1: Xem bản đồ Google Maps
        binding.btnOpenMap.setOnClickListener {
            val mapIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:16.0768,108.2141?q=Đại+học+Sư+phạm+Kỹ+thuật+Đà+Nẵng")
                setPackage("com.google.android.apps.maps")
            }
            try {
                startActivity(mapIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Không tìm thấy ứng dụng Bản đồ!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindData(s: Student) {
        binding.tvName.text = s.name
        binding.tvDetails.text = "MSSV: ${s.id} - Lớp: ${s.className}"
        binding.tvGpaBadge.text = "Điểm GPA: ${s.gpa}"
    }

    // Các hàm vòng đời để quan sát Logcat
    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart") }
}