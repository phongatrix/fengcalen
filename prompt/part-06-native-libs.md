# PART 06 — Native Libraries Loading (JNA/JNIEasy, .so files)

## Mục tiêu

Tải và dùng các thư viện native:
- `libandroidx.graphics.path.so` (Cocos2d-x paths kernel)
- `libdatastore_shared_counter.so`

Sử dụng JNA (Java Native Access) hoặc JNIEasy để load và gọi native methods.

## Thực hiện

```bash
mkdir -p app/src/main/jniLibs/arm64-v8a
mkdir -p app/src/main/jniLibs/x86_64
```

## Tạo app/src/main/jniLibs/arm64-v8a/load_native.kt

```kotlin
package fengcalen.native

import android.util.Log
import com.sun.jna.Library
import com.sun.jna.Native

private const val TAG = "NativeLibs"

interface GraphicsPathLibrary : Library {
    companion object {
        @JvmStatic
        fun load(context: android.content.Context): GraphicsPathLibrary {
            val libPath = "libandroidx.graphics.path.so"
            return try {
                Native.load(libPath, GraphicsPathLibrary::class.java)
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load native library", e)
                throw e
            }
        }
    }

    @Native.register("dlopen")
    external fun dlopen(filename: String?, flags: Int): Long

    @Native.register("dlsym")
    external fun dlsym(handle: Long, symbol: String?): Long

    @Native.register("dlclose")
    external fun dlclose(handle: Long): Int

    @Native.register("androidx_graphics_path_calculate")
    external fun calculatePath(points: Array<Float>, count: Int): Long

    @Native.register("androidx_graphics_path_free")
    external fun freePath(handle: Long)
}

interface DatastoreCounterLibrary : Library {
    companion object {
        @JvmStatic
        fun load(context: android.content.Context): DatastoreCounterLibrary {
            val libPath = "libdatastore_shared_counter.so"
            return try {
                Native.load(libPath, DatastoreCounterLibrary::class.java)
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load native library", e)
                throw e
            }
        }
    }

    @Native.register("datastore_counter_get")
    external fun getCounter(): Int

    @Native.register("datastore_counter_increment")
    external fun incrementCounter(): Int
}
```

## Tạo app/src/main/java/fengcalen/service/NativeService.kt

```kotlin
package fengcalen.service

import android.content.Context
import fengcalen.native.DatastoreCounterLibrary
import fengcalen.native.GraphicsPathLibrary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeService @Inject constructor() {
    private var graphicsPathLib: GraphicsPathLibrary? = null
    private var datastoreLib: DatastoreCounterLibrary? = null

    fun initialize(context: Context): Boolean {
        var success = true
        try {
            graphicsPathLib = GraphicsPathLibrary.load(context)
            Log.d("NativeService", "Loaded libandroidx.graphics.path.so")
        } catch (e: UnsatisfiedLinkError) {
            Log.w("NativeService", "libandroidx.graphics.path.so not available: ${e.message}")
            success = false
        }

        try {
            datastoreLib = DatastoreCounterLibrary.load(context)
            Log.d("NativeService", "Loaded libdatastore_shared_counter.so")
        } catch (e: UnsatisfiedLinkError) {
            Log.w("NativeService", "libdatastore_shared_counter.so not available: ${e.message}")
            success = false
        }

        return success
    }

    fun getGraphicsPathHandle(points: Array<Float>, count: Int): Long {
        return graphicsPathLib?.calculatePath(points, count) ?: 0L
    }

    fun getDatastoreCounter(): Int {
        return datastoreLib?.getCounter() ?: 0
    }

    fun incrementDatastoreCounter(): Int {
        return datastoreLib?.incrementCounter() ?: 0
    }

    fun cleanup() {
        graphicsPathLib?.dlclose(0L)
        datastoreLib?.dlclose(0L)
    }
}
```

## Kiểm tra Part 06

Sau khi chạy, kiểm tra:
- [ ] Cấu trúc thư mục `jniLibs/arm64-v8a/`
- [ ] `GraphicsPathLibrary.kt`, `DatastoreCounterLibrary.kt` có
- [ ] `NativeService.kt` có
- [ ] Native libs có thể load được

## Lệnh kiểm tra

```bash
cd /home/ibm/rea-analyze
echo "=== Kiểm tra Part 06 ==="
ls app/src/main/jniLibs/arm64-v8a/ 2>/dev/null
ls app/src/main/java/fengcalen/native/ 2>/dev/null
ls app/src/main/java/fengcalen/service/ 2>/dev/null
```
