package com.breens.foodappadmin.data.repositories

import com.breens.foodappadmin.common.Result
import com.breens.foodappadmin.data.model.Banner
import com.breens.foodappadmin.data.model.Card
import com.breens.foodappadmin.data.model.Cate
import com.breens.foodappadmin.data.model.Chat
import com.breens.foodappadmin.data.model.Nameuser
import com.breens.foodappadmin.data.model.Order
import com.breens.foodappadmin.data.model.Task
import com.breens.foodappadmin.data.model.User

interface Repository {
// Tai khoan
    suspend fun loginUser(email: String, password: String): Result<List<User>>
    suspend fun logoutUser(): Result<Unit>
    suspend fun registerUser(firstName:String, lastName: String,email: String, password: String): Result<Unit>
 //   Task
    suspend fun addCard(cate: String, image: String,title: String, body: String, price: Int, favorite: Int, views: Int, sale: Int): Result<Unit>
    suspend fun getAllCards(): Result<List<Card>>
    suspend fun deleteCard(cardId: String): Result<Unit>
    suspend fun updateCard(cate: String, image: String,title: String, body: String, price: Int, favorite: Int, views: Int, sale: Int, cardID: String): Result<Unit>
//   Task
    suspend fun addTask(image: String,title: String, body: String, price: Int): Result<Unit>
    suspend fun getAllTasks(): Result<List<Task>>
    suspend fun deleteTask(taskId: String): Result<Unit>
    suspend fun updateTask(image: String, title: String, body: String,price: Int, taskId: String): Result<Unit>

//   Category
    suspend fun addCate(imageCate: String,titleCate: String): Result<Unit>
    suspend fun getAllCates(): Result<List<Cate>>
    suspend fun deleteCate(cateId: String): Result<Unit>
    suspend fun updateCate(imageCate: String,titleCate: String, cateId  : String): Result<Unit>



//  Banner
    suspend fun addBanner(imageBanner: String,titleBanner: String): Result<Unit>
    suspend fun getAllBanner(): Result<List<Banner>>
    suspend fun deleteBanner(bannerId: String): Result<Unit>
    suspend fun updateBanner(imageBanner: String,titleBanner: String, bannerId  : String): Result<Unit>

//  Order
    suspend fun addOrder(address: String,imageOrder: String, titleOrder: String,price:  Int , quantity:  Int, paymentMethods: String, total: Int): Result<Unit>
    suspend fun getAllOrder(): Result<List<Order>>
    suspend fun updateStatus(address: String,imageOrder: String, titleOrder: String,price:  Int , quantity:  Int, paymentMethods: String, total: Int,status: String, orderId: String): Result<Unit>
//Chat
    suspend fun addMessage( senderID: String, message: String, direction : Boolean): Result<Unit>
    suspend fun getAllUser(): Result<List<Nameuser>>

    suspend fun getAllMessage(): Result<List<Chat>>

}


