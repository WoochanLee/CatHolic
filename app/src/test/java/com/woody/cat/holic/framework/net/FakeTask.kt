package com.woody.cat.holic.framework.net

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import java.util.concurrent.Executor


class FakeSuccessTask<T>(private val result: T): FakeTask<T>() {
    override fun isComplete(): Boolean = true
    override fun isSuccessful(): Boolean = true
    override fun isCanceled(): Boolean = false
    override fun getResult(): T = result
    override fun <X : Throwable?> getResult(p0: Class<X>): T = result
    override fun getException(): Exception?  = null
}

class FakeFailTask<T>(private val result: T): FakeTask<T>() {
    override fun isComplete(): Boolean = true
    override fun isSuccessful(): Boolean = false
    override fun isCanceled(): Boolean = false
    override fun getResult(): T = result
    override fun <X : Throwable?> getResult(p0: Class<X>): T = result
    override fun getException(): Exception = RuntimeException()
}

abstract class FakeTask<T>: Task<T>() {
    override fun addOnSuccessListener(p0: OnSuccessListener<in T>): Task<T> = this
    override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in T>): Task<T> = this
    override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in T>): Task<T> = this
    override fun addOnFailureListener(p0: OnFailureListener): Task<T> = this
    override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<T> = this
    override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<T> = this
}