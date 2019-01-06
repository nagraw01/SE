package com.example.mg156.smarttraveler

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.os.Environment.getExternalStorageDirectory
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


private var authListener: FirebaseAuth.AuthStateListener? = null
private var auth: FirebaseAuth? = null
private var firedb: FirebaseDatabase? = null
private var fireStorage: FirebaseStorage? = null

private val CHOOSE_IMAGE: Int = 101
private val CAMERA_IMAGE: Int = 201
private val REQUEST_ID_MULTIPLE_PERMISSIONS = 678
private val TAG = "PermissionSetup"

lateinit var ProfileImageFBUrl: String
lateinit var editfullName: EditText
lateinit var txtUserEmail: TextView
lateinit var btnProfileUpdate: Button
lateinit var imageProfile: ImageView

var userType: String = "user"

var selectedPhotoUri: Uri? = null

class ProfilePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title ="User Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //get firebase instances
        auth = FirebaseAuth.getInstance()
        firedb = FirebaseDatabase.getInstance()
        fireStorage = FirebaseStorage.getInstance()
        //

        editfullName = findViewById(R.id.profile_page_full_name)
        imageProfile = findViewById(R.id.profile_page_image)
        txtUserEmail = findViewById(R.id.profile_page_email_id)
        btnProfileUpdate = findViewById(R.id.profile_page_update_button)

        setProfileDetails()

        imageProfile.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(setupPermission()){
                    uploadImage()
                }
            }
        })

        btnProfileUpdate.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                saveUserInformation()
            }
        })


        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setProfileDetails() {
        val user = auth?.currentUser

        val profileRef = FirebaseDatabase.getInstance().reference.child("users").child(user!!.uid)

        profileRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    val user = dataSnapshot.getValue(Users::class.java)
                    editfullName.setText(user?.full_name)
                    txtUserEmail.setText(user?.user_email)
                    userType = user!!.type
                    ProfileImageFBUrl = user?.photo_url
                    Picasso.get().load(dataSnapshot.child("photo_url").value.toString()).fit().into(imageProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("loadPost:onCancelled ${error.toException()}")
            }
        })
    }

    private fun saveUserInformation() {
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val full_name = editfullName.text.toString()
        val user_email = txtUserEmail.text.toString()

        if(full_name.isEmpty()){
            editfullName.setError("Name required!")
            editfullName.requestFocus()
            return
        }


        val user = Users(full_name, ProfileImageFBUrl.toString(), user_email,uid, userType)
        ref.setValue(user).addOnSuccessListener {
            Toast.makeText(baseContext, "User Profile Updated!",
                    Toast.LENGTH_LONG).show()
        }
    }

    private fun setupPermission() : Boolean{
        val permissionCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        val permissionExternalStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        var listPermissionsNeeded = ArrayList<String>()

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to camera denied")
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionExternalStorage != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to storage denied")
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(arrayOfNulls<String>(listPermissionsNeeded.size)) ,REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                if (grantResults.size > 0) {
                    var permissionsDenied = ""
                    var i = 0
                    for (per in permissions) {
                        if (grantResults[i] === PackageManager.PERMISSION_DENIED) {
                            permissionsDenied += "\n" + per
                        }
                        i++
                    }

                    if(permissionsDenied == ""){
                        uploadImage()
                    }
                    else{
                        Toast.makeText(this,"Go to settings and enable permissions",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if(requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null ){
                selectedPhotoUri = data.data
                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
                imageProfile.setImageBitmap(bitmap)
                uploadImageToFirebaseStorage()
            }
            else if(requestCode == CAMERA_IMAGE && resultCode == Activity.RESULT_OK && data != null){
                selectedPhotoUri = data.data
                var extra = data.extras
                var bitmap = extra.get("data") as Bitmap
                getImageUri(this,bitmap)
                imageProfile.setImageBitmap(bitmap)
                uploadImageToFirebaseStorage()
            }
        }
        catch (error: Exception){
            Toast.makeText(this,"Please Try Again!!",Toast.LENGTH_LONG).show()
        }

    }

    fun getImageUri(inContext: Context, inImage: Bitmap) {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null)
        selectedPhotoUri =  Uri.parse(path)
    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) return
        val userId = FirebaseAuth.getInstance().uid?:""

        val ref = FirebaseStorage.getInstance().getReference("/images/$userId")
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("SignUp", "Successfully uploaded image")
                    ref.downloadUrl.addOnSuccessListener {
                        ProfileImageFBUrl = it.toString()
                    }
                }
                .addOnFailureListener{
                    Log.d(" SignUp ", "Failed to upload image to storage : ${it.message}")
                }
    }


    fun uploadImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                dialog.dismiss()
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_IMAGE)
            } else if (options[item] == "Choose From Gallery") {
                dialog.dismiss()
                var intent = Intent()
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_PICK)
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == R.id.home){
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
        }

        if (id == R.id.log_out) {
            signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    public override fun onStart() {
        super.onStart()
        auth?.addAuthStateListener(authListener!!)
    }

    public override fun onStop() {
        super.onStop()
        if (authListener != null) {
            auth?.removeAuthStateListener(authListener!!)
        }
    }
}
