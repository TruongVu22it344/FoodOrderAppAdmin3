package com.breens.foodappadmin.feature_tasks.ui.components

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.breens.foodappadmin.data.model.Task
import com.breens.foodappadmin.feature_tasks.state.TasksScreenUiState
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTaskDialogComponent(
    setTaskImage: (String) -> Unit,
    setTaskTitle: (String) -> Unit,
    setTaskBody: (String) -> Unit,
    setTaskPrice: (Int) -> Unit,
    saveTask: () -> Unit,
    closeDialog: () -> Unit,
    task: Task?,
    uiState: TasksScreenUiState,
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uiState.bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        }

    }
    Dialog(onDismissRequest = { closeDialog() }) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LazyColumn(contentPadding = PaddingValues(12.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Cập nhập",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                item{
                    Spacer(modifier = Modifier.height(12.dp))

                    uiState.bitmap. let {bitmap->
                        if(bitmap!=null){
                            updateImageTask(bitmap, context as ComponentActivity){ success, imageUrl ->
                                if(success){
                                    imageUrl.let{
                                        uiState.imgUrl= it

                                    }

                                    setTaskImage(uiState.imgUrl)
                                }

                            }
                        }
                    }


                    if(uiState.bitmap!=null){
                        Image(
                            bitmap = uiState.bitmap?.asImageBitmap()!!,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(160.dp)
                                .height(170.dp)
                        )
                    }
                    TextButton(
                        onClick = {

                            launcher.launch("image/*")
                        },
                        modifier = Modifier
                            .border(BorderStroke(2.dp, Color.Black))
                    ) {
                        Text(
                            text = "Chọn ảnh tại đây!",
                        )

                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.currentTextFieldTitle,
                        onValueChange = { title ->
                            setTaskTitle(title)
                        },
                        label = { Text("Tên món") },
                        placeholder = { Text(task?.title ?: "") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black,
                        ),
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.currentTextFieldBody,
                        onValueChange = { body ->
                            setTaskBody(body)
                        },
                        label = { Text("Mô tả") },
                        placeholder = { Text(task?.body ?: "") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black,
                        ),
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.currentTextFieldPrice.toString(),
                        onValueChange = {price->
                                        setTaskPrice(price.toInt())
                        },
                        label = { Text("Giá") },
                        placeholder = { Text("${task?.price ?: 0}") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black,
                        ),
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = {
                                saveTask()
                                setTaskTitle("")
                                setTaskBody("")
                                setTaskPrice(0)
                                closeDialog()
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White,
                            ),
                        ) {
                            Text(text = "Lưu")
                        }
                        Button(
                            onClick = {
                                closeDialog()
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White,
                            ),
                        ) {
                            Text(text = "Đóng")
                        }
                    }
                }
            }
        }
    }
}
fun updateImageTask(bitmap: Bitmap, context: ComponentActivity, calBack: (Boolean, String)->Unit) {
    val storageRef = Firebase.storage.reference
    val imageRef = storageRef.child("cards/${bitmap}")

    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val imageData = baos.toByteArray()

    imageRef.putBytes(imageData).addOnSuccessListener {
        imageRef.downloadUrl.addOnSuccessListener { uri->
            val imageUrl = uri.toString()
            calBack(true, imageUrl)

        }.addOnFailureListener {
            calBack(false, null.toString())
        }
    }.addOnFailureListener {
        calBack(false, null.toString())
    }
}
