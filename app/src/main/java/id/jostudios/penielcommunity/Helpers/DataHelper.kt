package id.jostudios.penielcommunity.Helpers

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.jostudios.penielcommunity.Models.SaveModel
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import java.io.File
import java.lang.reflect.Type

class DataHelper(context: Context) {
    private val gson: Gson = Gson();
    private var saveDir: String;
    private var saveFile: String;

    private val saveModelType: Type = object : TypeToken<SaveModel>() {}.type;

    init {
        saveDir = "${context.filesDir}";
        saveFile = "data.sav";
        System.debug("Save file path : ${saveDir}/${saveFile}");
    }

    public fun loadData() {
        val file = FileHelper.getFile(saveDir, saveFile);

        if (file == null) {
            val newFile = File("${saveDir}/${saveFile}");
            newFile.createNewFile();
            return;
        }

        val rawData = FileHelper.readFile(file);

        System.debug("Loading Data!");
        System.debug("Data : ${rawData}");

        if (TextUtils.isEmpty(rawData)) {
            return;
        }

        val objData = gson.fromJson<SaveModel>(rawData, saveModelType);

        System.debug("Load saved data!");
        System.debug("Raw Data : ${rawData}");
        System.debug("Obj Data : ${objData}");

        GlobalState.token = objData.token;
        GlobalState.currentUser = objData.user;
        GlobalState.currentCredential = objData.credential;
        GlobalState.isAuth = GlobalState.token != null;
        GlobalState.isLogin = GlobalState.currentCredential != null && GlobalState.currentUser != null;
    }

    public fun saveData(model: SaveModel) {
        val file = FileHelper.getFile(saveDir, saveFile);

        if (file == null) {
            val newFile = File("${saveDir}/${saveFile}");
            newFile.createNewFile();
        }

        val rawData = gson.toJson(model);

        FileHelper.writeFile(file!!, rawData);

        System.debug("Save current data!");
        System.debug("Raw Data : ${rawData}");
    }

//    public fun readSave() {
//        val saveFile = FileHelper.getFile(saveDir, saveFile)!!;
//        val rawSave = FileHelper.readFile(saveFile)!!;
//
//        System.debug("Raw Save : ${rawSave}");
//    }
//
//    public fun saveData(saveModel: SaveModel) {
//        val rawSave = gson.toJson(saveModel);
//
//        System.debug("Raw Json : ${rawSave}");
//
//        var file = FileHelper.getFile(saveDir, saveFile);
//        if (file == null) {
//            val newFile = File("${saveDir}/${saveFile}");
//            newFile.createNewFile();
//        }
//
//        FileHelper.writeFile(file!!, rawSave);
//    }
//
//    public fun loadData(): SaveModel? {
//        val readFile = FileHelper.getFile(saveDir, saveFile);
//
//        var file = FileHelper.getFile(saveDir, saveFile);
//        if (file == null) {
//            var model = SaveModel();
//            saveData(model);
//        }
//
//        return try {
//            var readRawSave = FileHelper.readFile(file!!);
//            var saveJson = gson.fromJson<SaveModel>(readRawSave, saveModelType);
//
//            saveJson;
//        } catch (e: Exception) {
//            null;
//        }
//    }
}