package org.iflab.ibistubydreamfactory.apis;

import org.iflab.ibistubydreamfactory.models.LostFound;
import org.iflab.ibistubydreamfactory.models.PostLostFoundSuccessModel;
import org.iflab.ibistubydreamfactory.models.Resource;
import org.iflab.ibistubydreamfactory.models.UploadFileRequestBody;
import org.iflab.ibistubydreamfactory.models.UploadSuccessModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 失物招领接口
 */
public interface LostFoundAPI {
    /**
     * 获得失物招领列表
     * page (列表页-1)的十倍，如要获取第二页的数据，那么page的值应该为10
     */
    @GET("ibistu/_table/module_lost_found?filter=isFound%3Dfalse&limit=10&order=createTime%20desc")
    Call<Resource<LostFound>> getLostFound(@Query("offset") String page);

    /**
     * 多图片上传
     *
     * @param uploadFileRequestBody 要上传的文件请求体，可以多文件上传
     */
    @POST("files/ibistu/lost_found/image/")
    Call<UploadSuccessModel> uploadFile(@Body UploadFileRequestBody uploadFileRequestBody);

    /**
     * 发布新的lostfound
     */
    @POST("ibistu/_table/module_lost_found")
    Call<PostLostFoundSuccessModel> postNewLostFound(@Body LostFound[] postLostFoundRequestBody);

}
