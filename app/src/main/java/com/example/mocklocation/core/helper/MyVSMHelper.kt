package com.example.mocklocation.core.helper

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import com.skt.tmap.vsm.config.ConfigurationData
import com.skt.tmap.vsm.config.ConfigurationDataManager
import com.skt.tmap.vsm.config.ConfigurationLoader
import com.skt.tmap.vsm.config.LoaderOptions
import com.skt.tmap.vsm.config.ResourceLoader
import com.skt.tmap.vsm.map.VSMMap
import timber.log.Timber
import java.lang.ref.WeakReference

class MyVSMHelper private constructor(context: Context) {
    private val mAppContext: Context = context.applicationContext
    private var mListener: WeakReference<InitializeListener>? = null
    private var mHandler: Handler
    private var mHandlerThread: HandlerThread
    private var mConfigurationLoader: ConfigurationLoader? = null
    private var mResourceLoader: ResourceLoader? = null
    private var mConfigurationData: ConfigurationData? = null
    private var mInitState = INIT_STATE_NONE

    // mCallback을 먼저 선언하여 mHandler 초기화 시점에 접근 가능하도록 합니다.
    private val mCallback = Handler.Callback { message ->
        when (message.what) {
            MSG_CONFIGURATION_LOADED -> {
                val data = message.obj as ConfigurationData
                handleConfigurationLoaded(data)
            }
            MSG_CONFIGURATION_ERROR -> {
                val errorCode = message.arg1
                handleConfigurationLoaderError(errorCode)
            }
            MSG_RESOURCES_PROGRESS -> {
                handleBaseResourceProgress(message.arg1, message.arg2)
            }
            MSG_RESOURCES_LOADED -> {
                handleBaseResourceLoaded()
            }
            MSG_RESOURCES_ERROR -> {
                val errorCode = message.arg1
                handleBaseResourceLoaderError(errorCode)
            }
        }
        false
    }

    companion object {
        private var sSingleton: MyVSMHelper? = null

        private const val CONFIG_BASE_URL_FARM43 = "http://farm43.ids.onestore.co.kr:8012/hub/vsm_dev"
        private const val LOADER_ID = 1
        private const val MSG_CONFIGURATION_LOADED = 1
        private const val MSG_CONFIGURATION_ERROR = 2
        private const val MSG_RESOURCES_PROGRESS = 5
        private const val MSG_RESOURCES_LOADED = 6
        private const val MSG_RESOURCES_ERROR = 7

        private const val INIT_STATE_NONE = 0
        private const val INIT_STATE_CONFIGURATION_LOADED = 1
        private const val INIT_STATE_RESOURCES_LOADED = 2
        private const val INIT_STATE_ENGINE_INITIALIZED = 3

        @JvmStatic
        @Synchronized // 멀티스레드 환경에서 안전하게 싱글턴을 생성하기 위해 동기화 처리
        fun createHelper(context: Context) {
            VSMMap.LoadLibrary()
            if (sSingleton == null) {
                sSingleton = MyVSMHelper(context)
            }
        }

        @JvmStatic
        fun getInstance(): MyVSMHelper = sSingleton ?: throw IllegalStateException("MyVSMHelper not initialized")
    }

    init {
        Timber.d("VSM Engine: ${VSMMap.getEngineVersion()}")

        mHandlerThread = HandlerThread("VSMHelper").apply { start() }
        mHandler = Handler(mHandlerThread.looper, mCallback)

        Timber.d("VSM Engine: ${VSMMap.getEngineVersion()}")

        stopLoading()
        VSMMap.createInstance(mAppContext)
    }

    interface InitializeListener {
        fun onProgress(count: Int, total: Int)
        fun onSuccess()
        fun onError(errorCode: Int, errorMsg: String)
    }

    fun setListener(listener: InitializeListener?) {
        mListener = if (listener != null) WeakReference(listener) else null
    }

    fun stopLoading() {
        mResourceLoader?.destory()
        mResourceLoader = null

        mConfigurationLoader?.destroy()
        mConfigurationLoader = null

        mHandler.removeCallbacksAndMessages(null)
    }

    fun initialize() {
        stopLoading()
        doNextState()
    }

    private fun loadConfiguration() {
        val options = LoaderOptions().apply {
            rootDir = mAppContext.getExternalFilesDir(null)?.absolutePath + "/MyVSM"
            cacheDir = "$rootDir/cache"
            dataDir = "$rootDir/data"
            fullMapDir = "$rootDir/embedded"
            configUrl = "$CONFIG_BASE_URL_FARM43/config/application/S001/TMAP_2020B.conf"
            baseUrlPrefix = CONFIG_BASE_URL_FARM43
            flags = LoaderOptions.LOADER_FLAG_MARK_BROKEN
            resourceVersion = 9
        }

        mConfigurationLoader = ConfigurationLoader(LOADER_ID, options).apply {
            load()
            setListener(object : ConfigurationLoader.ConfigurationLoaderListener {
                override fun onSuccess(configurationData: ConfigurationData) {
                    Timber.d("onSuccess")
                    val msg = mHandler.obtainMessage(MSG_CONFIGURATION_LOADED, configurationData)
                    mHandler.sendMessage(msg)
                }

                override fun onError(errorCode: Int) {
                    Timber.d("onError: $errorCode")
                    val msg = mHandler.obtainMessage(MSG_CONFIGURATION_ERROR, errorCode)
                    mHandler.sendMessage(msg)
                }
            })
        }
    }

    private fun loadResources() {
        var hasItemToDownload = false
        mConfigurationData?.let { data ->
            mResourceLoader = ResourceLoader(data).apply {
                val packageList = data.resourcePackages()
                for (pkg in packageList) {
                    val itemList = pkg.items()
                    for (item in itemList) {
                        if (item.offlineOnly()) continue
                        if ((!item.downloaded() && !item.optional()) || item.updated()) {
                            addItem(item)
                            hasItemToDownload = true
                        }
                    }
                }

                setListener(object : ResourceLoader.ResourceLoaderListener {
                    override fun onStart() {
                        Timber.d("Loading resources...")
                    }

                    override fun onProgress(count: Int, totalCount: Int, size: Long, totalSize: Long) {
                        Timber.d("Loading resources... ($count/$totalCount, $size / $totalSize)")
                        mHandler.obtainMessage(MSG_RESOURCES_PROGRESS, count, totalCount).sendToTarget()
                    }

                    override fun onSuccess() {
                        Timber.d("Resources are loaded.")
                        mHandler.sendEmptyMessage(MSG_RESOURCES_LOADED)
                    }

                    override fun onError(errorCode: Int) {
                        Timber.d("Failed to load base resources: errorCode=$errorCode")
                    }

                    override fun onFinished() {
                        Timber.d("Resources onFinished")
                    }
                })
            }
        }

        if (hasItemToDownload) {
            mResourceLoader?.load()
        } else {
            mHandler.sendEmptyMessage(MSG_RESOURCES_LOADED)
        }
    }

    private fun initEngine() {
        mConfigurationData?.let { data ->
            ConfigurationDataManager.setActiveConfigurationData(data, null)
        }

        if (VSMMap.getInstance().Init()) {
            Timber.d("VSM Engine is initialized!!!")
        }

        mInitState = INIT_STATE_ENGINE_INITIALIZED
        doNextState()
    }

    private fun destroyEngine() {
        VSMMap.removeInstance()

        mResourceLoader?.destory()
        mResourceLoader = null

        mConfigurationLoader?.destroy()
        mConfigurationLoader = null

        ConfigurationDataManager.setActiveConfigurationData(null, null)
        mConfigurationData = null

        mHandler.removeCallbacksAndMessages(null)
    }

    private fun doNextState() {
        Timber.d("doNextState: mInitState == $mInitState")
        when (mInitState) {
            INIT_STATE_NONE -> loadConfiguration()
            INIT_STATE_CONFIGURATION_LOADED -> loadResources()
            INIT_STATE_RESOURCES_LOADED -> initEngine()
            INIT_STATE_ENGINE_INITIALIZED -> {
                mListener?.get()?.onSuccess()
            }
        }
    }

    private fun handleConfigurationLoaded(data: ConfigurationData) {
        mConfigurationLoader = null
        mConfigurationData = data
        Timber.d("Load configuration done")
        mInitState = INIT_STATE_CONFIGURATION_LOADED
        doNextState()
    }

    private fun handleBaseResourceProgress(count: Int, total: Int) {
        mListener?.get()?.onProgress(count, total)
    }

    private fun handleConfigurationLoaderError(errorCode: Int) {
        mConfigurationLoader = null
        mListener?.get()?.onError(errorCode, "ConfigurationLoader Error")
    }

    private fun handleBaseResourceLoaded() {
        mResourceLoader = null
        mInitState = INIT_STATE_RESOURCES_LOADED
        doNextState()
    }

    private fun handleBaseResourceLoaderError(errorCode: Int) {
        mResourceLoader = null
        mListener?.get()?.onError(errorCode, "ResourceLoader Error")
    }
}