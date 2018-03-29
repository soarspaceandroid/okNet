package com.soar.soarlibrary.net

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.soar.soarlibrary.model.BaseBean
import com.soar.soarlibrary.utils.GsonFactory
import com.yonglibao.corelibrary.db.DataCacheManager
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/9
 *※ Time : 14:22
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.corelibrary.http
 *
 * 网络框架 接入的入口类 , 其他类都是辅助类跟 OKHttp有关, 删除OKhttp 其他辅助类都可以删除,
 *----------------------------------------------------
 */
class RequestHelper<T> {

    private var bodyBuilder = MultipartBody.Builder()
    private var requestBuilder: Request.Builder = Request.Builder()
    private lateinit var okLissenter: OkLissenter
    private lateinit var requestConfig: RequestConfig
    private val handler: Handler = Handler(Looper.myLooper())

    companion object {
        val TAG_DOWN_FILE = "down_file"

        fun create():RequestHelper<BaseBean>{
            return lazy { RequestHelper<BaseBean>() }.value
        }
    }

    constructor(){
        bodyBuilder?.setType(MultipartBody.ALTERNATIVE)
    }

    /**
     * reqeust for data
     */
    fun requestParams(requestConfig: RequestConfig):RequestHelper<T>{
        this.requestConfig = requestConfig
        requestBuilder.url(RequestPartsInter.getRequestParts().getUrl() + requestConfig.api)
        var paramsBody = FormBody.Builder()
        if(requestConfig?.params.isNotEmpty()) {
            var iter = requestConfig?.params?.entries?.iterator()
            while (iter?.hasNext()) {
                var item = iter?.next()
                paramsBody.add(item?.key, item?.value)
            }
        }
        bodyBuilder?.addPart(Headers.of(
                "Content-Disposition",
                "form-data; name=\"params\"")
                ,paramsBody.build())
        return this
    }


    /**
     * request for uploading file
     */
    fun requestUploadFile(key:String , file:File):RequestHelper<T>{
        val type = MediaType.parse("application/octet-stream")//"text/xml;charset=utf-8"
        val fileBody = RequestBody.create(type, file)
        bodyBuilder.addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"${key}\"; filename=\"${file.name}\"")
                        , fileBody)
        return this
    }


    /**
     * request for download file
     */
    fun requestDownloadFile(url:String):RequestHelper<T>{
        requestBuilder.url(url)
        return this
    }


    /**
     * tag reqeust
     */
    fun tag(tag:String) :RequestHelper<T>{
        requestBuilder.tag(tag)
        return this
    }

    /**
     * reqeust header
     */
    fun header(headers:Map<String , String>):RequestHelper<T>{
        if(headers.isNotEmpty()) {
            var iter = headers?.entries.iterator()
            while (iter?.hasNext()) {
                var item = iter?.next()
                requestBuilder.addHeader(item?.key, item?.value)
            }
        }

        return this
    }




    /**
     * for lis
     */
    fun lisenter(okLissenter: OkLissenter):RequestHelper<T>{
        this.okLissenter = okLissenter
        return this
    }

    /**
     * to reqeust
     */
    fun toReqeust(){


        when(okLissenter){
            is RequestLisenter<*> -> {
                //从缓存获取
                if (requestConfig?.firstCache) {
                    var data = DataCacheManager.getInstance().getObject(requestConfig?.clz) as T
                    data?.let {
                        (okLissenter as RequestLisenter<T>).onBindData(data)
                    }

                }
            }
            is DownloadLisenter -> {
                requestBuilder.tag(TAG_DOWN_FILE)
                (okLissenter as DownloadLisenter).onStart()
            }
        }

        requestBuilder?.post(bodyBuilder?.build())
        val request = requestBuilder?.build()

        OkHttpClientCreator.getClient().newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                handler?.post {
                    when(okLissenter){
                        is RequestLisenter<*> -> (okLissenter as RequestLisenter<T>)?.onError(e)
                        is DownloadLisenter -> (okLissenter as DownloadLisenter)?.onFail(e)
                    }

                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                when(okLissenter){
                    is RequestLisenter<*> -> {parasData(request , response , okLissenter as RequestLisenter<T>)}
                    is DownloadLisenter -> {parasFile(response , okLissenter as DownloadLisenter)}
                }

            }
        })
    }


    fun parasFile(response: Response? , downloadLisenter: DownloadLisenter){
        var index: Int = -1
        var bytes = ByteArray(1024)
        var downloadFile:FileOutputStream? = null
        var inputStream:InputStream? = null
        var file = downloadLisenter.getOutputFile()
        try {
            downloadFile = FileOutputStream(file)
            var responseBody = response?.body()
            inputStream = responseBody?.byteStream()
            val totalLength = responseBody?.contentLength()
            var currentLength = 0
            while (index != -1) {
                index = inputStream?.read(bytes)!!
                downloadFile.write(bytes, 0, index)
                downloadFile.flush()
                currentLength += index
                var progress = (currentLength * 1.0f / totalLength!! * 100) as Int
                downloadLisenter.onProgress(progress)
            }

            downloadLisenter.onSuccess(file)

        }catch (e:Exception){
            downloadLisenter.onFail(e)
        }finally {
            if(downloadFile!= null) {
                downloadFile.close()
            }
            if(inputStream != null) {
                inputStream?.close()
            }
        }

    }


    fun parasData(request:Request ,response: Response? ,requestLisenter:RequestLisenter<T>){
        var resData = response?.body()?.string()
        try {
            val data = GsonFactory.create().fromJson(resData, requestConfig?.clz) as T
            Log.d("Request", request.url().toString() + "\n Model : ${data.toString()}\n" + resData)
            if (data is BaseBean) {
                if (data?.code == ResponseCode.RESPONSE_SUCCESS) {
                    handler?.post {
                        requestLisenter?.onBindData(data)
                        data?.isCache = true
                        DataCacheManager.getInstance().insertObject(data)
                    }
                } else {
                    handler?.post { requestLisenter?.onFail(data) }
                }
            }
        }catch (e:Exception){
            handler?.post {
                requestLisenter?.onError(e)
            }
        }
    }



    fun setOnAllReqeustFinish(idleCallback : Runnable ){
        OkHttpClientCreator.getClient().dispatcher().setIdleCallback(idleCallback)
    }

}