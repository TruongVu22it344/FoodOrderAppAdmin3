package com.breens.foodappadmin.data.repositories

import android.content.ContentValues
import android.util.Log
import com.breens.foodappadmin.common.COLLECTION_PATH_NAME
import com.breens.foodappadmin.common.COLLECTION_PATH_NAME_ACCOUNT
import com.breens.foodappadmin.common.COLLECTION_PATH_NAME_BANNER
import com.breens.foodappadmin.common.COLLECTION_PATH_NAME_CARD
import com.breens.foodappadmin.common.COLLECTION_PATH_NAME_CATEGORY
import com.breens.foodappadmin.common.COLLECTION_PATH_NAME_ORDER
import com.breens.foodappadmin.common.PLEASE_CHECK_INTERNET_CONNECTION
import com.breens.foodappadmin.common.Result
import com.breens.foodappadmin.common.convertDateFormat
import com.breens.foodappadmin.common.getCurrentTimeAsString
import com.breens.foodappadmin.data.model.Banner
import com.breens.foodappadmin.data.model.Card
import com.breens.foodappadmin.data.model.Cate
import com.breens.foodappadmin.data.model.Chat
import com.breens.foodappadmin.data.model.Nameuser

import com.breens.foodappadmin.data.model.Order
import com.breens.foodappadmin.data.model.Task
import com.breens.foodappadmin.data.model.User
import com.breens.foodappadmin.di.IoDispatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject


class RepositoryImpl @Inject constructor(
    private val foodDB: FirebaseFirestore,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : Repository {
    override suspend fun loginUser(email: String, password: String): Result<List<User>> {
        return try {
            withContext(ioDispatcher) {

                val authResult = try {
                    firebaseAuth.signInWithEmailAndPassword(email, password).await()
                } catch (e: Exception) {
                    // Handle failed login attempt (e.g., wrong password, no network, etc.)
                    Log.d("ERROR: ", "Login failed: ${e.message}")
                    return@withContext Result.Failure(e)
                }

                // If sign-in was successful, proceed to fetch users
                if (authResult.user != null) {
                    val users = try {
                        foodDB.collection(COLLECTION_PATH_NAME_ACCOUNT)
                            .whereEqualTo("email", email)
                            .get()
                            .await()
                            .documents.map { document ->
                                User(
                                    userID = document.id,
                                    firstName = document.getString("firstName") ?: "",
                                    lastName  = document.getString("lastName") ?: "",
                                    email  = document.getString("email") ?: "",
                                    role  = document.get("role")?.let {
                                        when (it) {
                                            is Long -> it.toInt()
                                            is Double -> it.toInt()
                                            is String -> it.toIntOrNull() ?: 0
                                            else -> 0
                                        }
                                    } ?: 0,
                                )
                            }
                    } catch (e: Exception) {
                        // Handle failed fetching from Firestore
                        Log.d("ERROR: ", "Failed to fetch user data: ${e.message}")
                        return@withContext Result.Failure(e)
                    }

                    Result.Success(users)
                } else {
                    // Handle null user scenario (unlikely in this context but good practice)
                    Result.Failure(IllegalStateException("Unknown error, user is null after successful login"))
                }
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }
    override suspend fun logoutUser(): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val authResult = withTimeoutOrNull(10000L) {
                    firebaseAuth.signOut()
                }

                if (authResult == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }
    /*.document("8kbrzXslDixuiEKcNpXC") // Giả sử userID được sử dụng làm ID của phần tử mẹ
    .collection("orders") // Giả sử "orders" là tên của collection chứa các đơn hàng
    .add(order)*/
    override suspend fun registerUser(firstName:String, lastName: String,email: String, password: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val addAccountTimeout = withTimeoutOrNull(10000L) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val role = 1
                                val userProfile = hashMapOf(
                                    "firstName" to firstName,
                                    "lastName" to lastName,
                                    "email" to email,
                                    "role" to role
                                )
                                foodDB.collection(COLLECTION_PATH_NAME_ACCOUNT)
                                    .add(userProfile)

                            } else {
                                // Handle sign-up failure

                            }
                        }

                }

                if (addAccountTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }
    override suspend fun addBanner(imageBanner: String, titleBanner: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val banner = hashMapOf(
                    "imageBanner" to imageBanner,
                    "titleBanner" to titleBanner,
                    "createdAtBanner" to getCurrentTimeAsString(),
                )

                val addBannerTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_BANNER)
                        .add(banner)
                }

                if (addBannerTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun getAllBanner(): Result<List<Banner>> {
        return try {
            withContext(ioDispatcher) {
                val fetchingBannerTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_BANNER)
                        .get()
                        .await()
                        .documents.map { document ->
                            Banner(
                                bannerId = document.id,
                                imageBanner = document.getString("imageBanner") ?: "",
                                titleBanner = document.getString("titleBanner") ?: "",
                                createdAt = convertDateFormat(
                                    document.getString("createdAtBanner") ?: "",
                                ),
                            )
                        }
                }

                if (fetchingBannerTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Log.d("BANNERS: ", "${fetchingBannerTimeout?.toList()}")

                Result.Success(fetchingBannerTimeout?.toList() ?: emptyList())
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun updateBanner(
        imageBanner: String,
        titleBanner: String,
        bannerId: String
    ): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val taskUpdate: Map<String, Any> = hashMapOf(
                    "imageBanner" to imageBanner,
                    "titleBanner" to titleBanner,
                )

                val addTaskTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_BANNER)
                        .document(bannerId)
                        .update(taskUpdate)
                }

                if (addTaskTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }
    override suspend fun deleteBanner(bannerId: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val addCateTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_BANNER)
                        .document(bannerId)
                        .delete()
                }

                if (addCateTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun addCate(imageCate: String, titleCate: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val category = hashMapOf(
                    "imageCate" to imageCate,
                    "titleCate" to titleCate,
                    "createdAt" to getCurrentTimeAsString(),
                )

                val addCategoryTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_CATEGORY)
                        .add(category)
                }

                if (addCategoryTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun getAllCates(): Result<List<Cate>> {
        return try {
            withContext(ioDispatcher) {
                val fetchingCategoryTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_CATEGORY)
                        .get()
                        .await()
                        .documents.map { document ->
                            Cate(
                                cateId = document.id,
                                imageCate = document.getString("imageCate") ?: "",
                                titleCate = document.getString("titleCate") ?: "",
                                createdAt = convertDateFormat(
                                    document.getString("createdAt") ?: "",
                                ),
                            )
                        }
                }

                if (fetchingCategoryTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Log.d("CATEGORIES: ", "${fetchingCategoryTimeout?.toList()}")

                Result.Success(fetchingCategoryTimeout?.toList() ?: emptyList())
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun updateCate(
        imageCate: String,
        titleCate: String,
        cateId: String
    ): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val taskUpdate: Map<String, Any> = hashMapOf(
                    "imageCate" to imageCate,
                    "titleCate" to titleCate,
                )

                val addTaskTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_CATEGORY)
                        .document(cateId)
                        .update(taskUpdate)
                }

                if (addTaskTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }
    override suspend fun deleteCate(cateId: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val addCateTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_CATEGORY)
                        .document(cateId)
                        .delete()
                }

                if (addCateTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }
    override suspend fun addTask(image: String, title: String, body: String, price: Int): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val task = hashMapOf(
                    "image" to image,
                    "title" to title,
                    "body" to body,
                    "price" to price,
                    "createdAt" to getCurrentTimeAsString(),
                )

                val addTaskTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME)
                        .add(task)
                }

                if (addTaskTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun getAllTasks(): Result<List<Task>> {
        return try {
            withContext(ioDispatcher) {
                val fetchingTasksTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME)
                        .get()
                        .await()
                        .documents.map { document ->
                            Task(
                                taskId = document.id,
                                image = document.getString("image") ?: "",
                                title = document.getString("title") ?: "",
                                body = document.getString("body") ?: "",
                                price = document.get("price")?.let {
                                    when (it) {
                                        is Long -> it.toInt()
                                        is Double -> it.toInt()
                                        is String -> it.toIntOrNull() ?: 0
                                        else -> 0
                                    }
                                } ?: 0,
                                createdAt = convertDateFormat(
                                    document.getString("createdAt") ?: "",
                                ),
                            )
                        }
                }

                if (fetchingTasksTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Log.d("TASKS: ", "${fetchingTasksTimeout?.toList()}")

                Result.Success(fetchingTasksTimeout?.toList() ?: emptyList())
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val addTaskTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME)
                        .document(taskId)
                        .delete()
                }

                if (addTaskTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun updateTask(image: String, title: String, body: String,price: Int, taskId: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val taskUpdate: Map<String, Any> = hashMapOf(
                    "image" to image,
                    "title" to title,
                    "body" to body,
                )

                val addTaskTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME)
                        .document(taskId)
                        .update(taskUpdate)
                }

                if (addTaskTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun addCard(
        cate:String,
        image: String,
        title: String,
        body: String,
        price: Int,
        favorite: Int,
        views: Int,
        sale: Int
    ): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val card = hashMapOf(
                    "cate" to cate,
                    "image" to image,
                    "title" to title,
                    "body" to body,
                    "price" to price,
                    "favorite" to favorite,
                    "views" to views,
                    "sale" to sale,
                    "createdAt" to getCurrentTimeAsString(),
                )

                val addTaskTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_CARD)
                        .add(card)
                }

                if (addTaskTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun getAllCards(): Result<List<Card>> {
        return try {
            withContext(ioDispatcher) {
                val fetchingTasksTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_CARD)
                        .get()
                        .await()
                        .documents.map { document ->
                            Card(
                                cardId = document.id,
                                cate = document.getString("cate") ?: "",
                                imageCard = document.getString("image") ?: "",
                                titleCard = document.getString("title") ?: "",
                                bodyCard = document.getString("body") ?: "",
                                priceCard = document.get("price")?.let {
                                    when (it) {
                                        is Long -> it.toInt()
                                        is Double -> it.toInt()
                                        is String -> it.toIntOrNull() ?: 0
                                        else -> 0
                                    }
                                } ?: 0,
                                favorite = document.get("favorite")?.let {
                                    when (it) {
                                        is Long -> it.toInt()
                                        is Double -> it.toInt()
                                        is String -> it.toIntOrNull() ?: 0
                                        else -> 0
                                    }
                                } ?: 0,
                                views = document.get("views")?.let {
                                    when (it) {
                                        is Long -> it.toInt()
                                        is Double -> it.toInt()
                                        is String -> it.toIntOrNull() ?: 0
                                        else -> 0
                                    }
                                } ?: 0,
                                sale = document.get("sale")?.let {
                                    when (it) {
                                        is Long -> it.toInt()
                                        is Double -> it.toInt()
                                        is String -> it.toIntOrNull() ?: 0
                                        else -> 0
                                    }
                                } ?: 0,
                                createdAt = convertDateFormat(
                                    document.getString("createdAt") ?: "",
                                ),
                            )
                        }
                }

                if (fetchingTasksTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Log.d("CARDS: ", "${fetchingTasksTimeout?.toList()}")

                Result.Success(fetchingTasksTimeout?.toList() ?: emptyList())
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }
    override suspend fun deleteCard(cardId: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val addTaskTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_CARD)
                        .document(cardId)
                        .delete()
                }

                if (addTaskTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }

    override suspend fun updateCard(
        cate:String,
        image: String,
        title: String,
        body: String,
        price: Int,
        favorite: Int,
        views: Int,
        sale: Int,
        cardID: String
    ): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val cardUpdate: Map<String, Any> = hashMapOf(
                    "cate" to cate,
                    "image" to image,
                    "title" to title,
                    "body" to body,
                    "price" to price,
                    "favorite" to favorite,
                    "views" to views,
                    "sale" to sale,
                    "createdAt" to getCurrentTimeAsString(),
                )
                val addTaskTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_CARD)
                        .document(cardID)
                        .update(cardUpdate)
                }

                if (addTaskTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }


    override suspend fun addOrder(
        address: String,
        imageOrder: String,
        titleOrder: String,
        price: Int,
        quantity: Int,
        paymentMethods: String,
        total: Int
    ): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val order = hashMapOf(
                    "address" to address,
                    "imageOrder" to imageOrder,
                    "title" to titleOrder,
                    "price" to price,
                    "quantity" to quantity,
                    "paymentMethods" to paymentMethods,
                    "total" to total,
                    "createdAt" to getCurrentTimeAsString(),
                )

                val addOrderTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_ORDER)
                        .add(order)
                }

                if (addOrderTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }



    override suspend fun getAllOrder(): Result<List<Order>> {
        return try {
            withContext(ioDispatcher) {
                val fetchingOrdersTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_ORDER)
                        .get()
                        .await()
                        .documents.map { document ->
                            Order(
                                orderId = document.id,
                                address = document.getString("address") ?: "",
                                imageOrder = document.getString("imageOrder") ?: "",
                                titleOrder = document.getString("title") ?: "",
                                paymentMethods = document.getString("paymentMethods") ?: "",
                                price = document.get("price")?.let {
                                    when (it) {
                                        is Long -> it.toInt()
                                        is Double -> it.toInt()
                                        is String -> it.toIntOrNull() ?: 0
                                        else -> 0
                                    }
                                } ?: 0,
                                quantity = document.get("quantity")?.let {
                                    when (it) {
                                        is Long -> it.toInt()
                                        is Double -> it.toInt()
                                        is String -> it.toIntOrNull() ?: 0
                                        else -> 0
                                    }
                                } ?: 0,
                                total = document.get("total")?.let {
                                    when (it) {
                                        is Long -> it.toInt()
                                        is Double -> it.toInt()
                                        is String -> it.toIntOrNull() ?: 0
                                        else -> 0
                                    }
                                } ?: 0,
                                createdAt = convertDateFormat(
                                    document.getString("createdAt") ?: "",
                                ),
                                status = document.getString("status") ?: ""
                            )
                        }
                }

                if (fetchingOrdersTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Log.d("ORDERS: ", "${fetchingOrdersTimeout?.toList()}")

                Result.Success(fetchingOrdersTimeout?.toList() ?: emptyList())
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }
    override suspend fun updateStatus(address: String,imageOrder: String, titleOrder: String,price:  Int , quantity:  Int, paymentMethods: String, total: Int,status: String, orderId: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val statusUpdate: Map<String, Any> = hashMapOf(
                    "address" to address,
                    "imageOrder" to imageOrder,
                    "title" to titleOrder,
                    "price" to price,
                    "quantity" to quantity,
                    "paymentMethods" to paymentMethods,
                    "total" to total,
                    "status" to status,
                )

                val addStatusTimeout = withTimeoutOrNull(10000L) {
                    foodDB.collection(COLLECTION_PATH_NAME_ORDER)
                        .document(orderId)
                        .update(statusUpdate)
                }

                if (addStatusTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }
    override suspend fun addMessage(
        senderID: String,
        message: String,
        direction: Boolean
    ): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val message = hashMapOf(
                    "senderID" to senderID,
                    "message" to message,
                    "direction" to direction,
                    "createdAt" to getCurrentTimeAsString(),
                )

                val addOrderTimeout = withTimeoutOrNull(10000L) {
                    val dbRef = firebaseDatabase.reference.child("nguyenvu1")
                    val contactRef = dbRef.child((getCurrentTimeAsString()))
                    contactRef.setValue(message)

                }

                if (addOrderTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)

                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                }

                Result.Success(Unit)
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")

            Result.Failure(exception = exception)
        }
    }


    override suspend fun getAllUser(): Result<List<Nameuser>> {
        return try {
            withContext(ioDispatcher) {

                val fetchingOrdersTimeout = withTimeoutOrNull(10000L) {

                    val contactRefs = firebaseDatabase.reference
                    val deferred = CompletableDeferred<List<Nameuser>>()

                    contactRefs.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val list = mutableListOf<Nameuser>()
                            dataSnapshot.children.forEach { data ->
                                val chat = Nameuser(
                                    Nameuser = data.key ?: "",
                                )
                                /*val chat = Chat(
                                    chatID = data.key ?: "",
                                    senderID = data.child("senderID").value as? String ?: "",
                                    direction = data.child("direction").value as? Boolean?: false,
                                    receiveID = data.child("receiveID").value as? String ?: "",
                                    message = data.child("message").value as? String ?: "",
                                    createdAt = convertDateFormat(data.child("createdAt").value as? String ?: ""),
                                )*/
                                list.add(chat)
//                                 reversedList = chatList.reversed()\

                            }

                            deferred.complete(list)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                            deferred.completeExceptionally(error.toException())
                        }
                    })

                    deferred.await()

                }

                if (fetchingOrdersTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)
                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                } else {
                    Log.d("MESSAGES: ", "${fetchingOrdersTimeout}")
                    Result.Success(fetchingOrdersTimeout)
                }
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")
            Result.Failure(exception = exception)
        }
    }
    override suspend fun getAllMessage(): Result<List<Chat>> {
        return try {
            withContext(ioDispatcher) {

                val fetchingOrdersTimeout = withTimeoutOrNull(10000L) {
                    val contactRefs = firebaseDatabase.reference.child("nguyenvu1")
                    val deferred = CompletableDeferred<List<Chat>>()

                    contactRefs.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val chatList = mutableListOf<Chat>()
                            dataSnapshot.children.forEach { data ->

                                val chat = Chat(
                                    chatID = data.key ?: "",
                                    senderID = data.child("senderID").value as? String ?: "",
                                    direction = data.child("direction").value as? Boolean?: false,
                                    receiveID = data.child("receiveID").value as? String ?: "",
                                    message = data.child("message").value as? String ?: "",
                                    createdAt = convertDateFormat(data.child("createdAt").value as? String ?: ""),
                                )
                                chatList.add(chat)
//                                 reversedList = chatList.reversed()\

                            }

                            deferred.complete(chatList)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                            deferred.completeExceptionally(error.toException())
                        }
                    })

                    deferred.await()

                }

                if (fetchingOrdersTimeout == null) {
                    Log.d("ERROR: ", PLEASE_CHECK_INTERNET_CONNECTION)
                    Result.Failure(IllegalStateException(PLEASE_CHECK_INTERNET_CONNECTION))
                } else {
                    Log.d("MESSAGES: ", "${fetchingOrdersTimeout}")
                    Result.Success(fetchingOrdersTimeout)
                }
            }
        } catch (exception: Exception) {
            Log.d("ERROR: ", "$exception")
            Result.Failure(exception = exception)
        }
    }



}
