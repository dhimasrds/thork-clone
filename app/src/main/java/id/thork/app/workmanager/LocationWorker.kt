package id.thork.app.workmanager

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber

/**
 * Created by M.Reza Sulaiman on 08/03/21
 * Jakarta, Indonesia.
 */
class LocationWorker(val context: Context, val workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    private val TAG = LocationWorker::class.java.name

    private val MAX_RUN_ATTEMPT = 6

    override fun doWork(): Result {
        try {
            if (runAttemptCount > MAX_RUN_ATTEMPT) {
                Result.failure()
            }

            val laborcode = inputData.getString("laborcode")
            val crewId = inputData.getString("crewId")
            val longitude = inputData.getString("longitude")
            val latitude = inputData.getString("latitude")
            val tag = inputData.getString("tag")

            if (!laborcode.isNullOrEmpty() && !crewId.isNullOrEmpty()) {
                Timber.tag(TAG).i("LocationWorker doWork() receive notification longitude: %s", longitude)
                Timber.tag(TAG).i("LocationWorker doWork() receive notification latitude: %s", latitude)

                val outputData = Data.Builder()
                    .putString("laborcode", laborcode)
                    .putString("crewId", crewId)
                    .putString("longitude", longitude)
                    .putString("latitude", latitude)
                    .putString("tag", tag)
                    .build()

                return Result.success(outputData)
            } else {
                return Result.retry()
            }

        } catch (e: Exception) {
            Timber.tag(TAG).e("doWork() error: %s", e.message)
            return Result.retry()
        }
    }
}