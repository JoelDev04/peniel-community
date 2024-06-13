package id.jostudios.penielcommunity.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView.GONE
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import id.jostudios.penielcommunity.Enums.FeedType
import id.jostudios.penielcommunity.Helpers.FileHelper
import id.jostudios.penielcommunity.Helpers.StorageHelper
import id.jostudios.penielcommunity.Models.FirebaseModels.FeedPostModel
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.R
import id.jostudios.penielcommunity.ViewModels.UploadViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class UploadFeedActivity : AppCompatActivity() {

    private lateinit var imgBtnBack: ImageView;

    private lateinit var btnUploadThread: Button;
    private lateinit var btnPickImage: Button;
    private lateinit var btnEditImage: Button;
    private lateinit var btnRemoveImage: Button;

    private lateinit var imgPickDisplay: ImageView;

    private lateinit var editTextCaption: EditText;
    private lateinit var editTextContent: EditText;

    private lateinit var viewModel: UploadViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_feed)

        initialize();
        widgetHandler();
    }

    @SuppressLint("Recycle")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2004 && resultCode == RESULT_OK && data != null) {
            System.debug("Result from pick image");
            System.debug("URI : ${data.data}");
            System.debug("Data : ${data.toString()}");

            System.debug("Image Path : ${data.data?.path}");

            val resolver = applicationContext.contentResolver;
            val stream = resolver.openInputStream(data.data!!);
            val bitmap = BitmapFactory.decodeStream(stream);

            if (bitmap.height >= 3000) {
                System.dialogMessageBox(this@UploadFeedActivity, "Error", "Gambar tidak dapat di gunakan!");
                return;
            }

            displayImage(data.data!!);
            viewModel.setSelectedImage(data.data!!);
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initialize() {
        imgBtnBack = findViewById(R.id.img_btn_back);

        btnUploadThread = findViewById(R.id.btn_upload_thread);
        btnPickImage = findViewById(R.id.btn_pick_image);
        btnEditImage = findViewById(R.id.btn_edit_image);
        btnRemoveImage = findViewById(R.id.btn_remove_image);

        imgPickDisplay = findViewById(R.id.img_pick_display);

        editTextContent = findViewById(R.id.edit_text_content);
        editTextCaption = findViewById(R.id.edit_text_caption);

        viewModel = ViewModelProvider(this)[UploadViewModel::class.java];
    }

    private fun widgetHandler() {
        editTextCaption.doOnTextChanged { text, start, before, count ->
            viewModel.setCaption(text.toString());
        }

        editTextContent.doOnTextChanged { text, start, before, count ->
            viewModel.setContent(text.toString());
        }

        imgBtnBack.setOnClickListener {
            finish();
        }

        btnUploadThread.setOnClickListener {
            if (TextUtils.isEmpty(viewModel.caption().value)) {
                System.dialogMessageBox(this, "Error", "Tolong isi caption!");
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(viewModel.content().value)) {
                System.dialogMessageBox(this, "Error", "Tolong isi content!");
                return@setOnClickListener
            }

            GlobalScope.launch {
                viewModel.uploadThread(this@UploadFeedActivity);

                finish();
            }
        }

        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*")
            startActivityForResult(intent, 2004);
        }

        btnEditImage.setOnClickListener {
            System.setToast(applicationContext, "Fitur ini belum tersedia!");
//
//            val editIntent = Intent(Intent.ACTION_EDIT).apply {
//                setDataAndType(viewModel.selectedImage().value, "image/*")
//                putExtra(Intent.EXTRA_STREAM, viewModel.selectedImage().value)
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//
//            startActivityForResult(Intent.createChooser(editIntent, "Edit image"), 2005);
        }

        btnRemoveImage.setOnClickListener {
            imgPickDisplay.setImageBitmap(null);
            viewModel.setSelectedImage(null);
            System.setToast(applicationContext, "Gambar telah di hapus!");
        }
    }

    private fun displayImage(uri: Uri) {
        val resolver = applicationContext.contentResolver
        val stream = resolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(stream)

        if (bitmap.height >= 3000) {
            System.dialogMessageBox(this@UploadFeedActivity, "Error", "Gambar tidak dapat di gunakan!")
            return
        }

        imgPickDisplay.setImageBitmap(bitmap)
    }
}