package com.kimiffy.cn.biubiu.http;

import com.kimiffy.cn.biubiu.base.BaseBean;
import com.kimiffy.cn.biubiu.bean.ArticleBean;
import com.kimiffy.cn.biubiu.bean.UserBean;
import com.kimiffy.cn.biubiu.bean.WxArticleListBean;
import com.kimiffy.cn.biubiu.bean.WxTitleBean;
import com.kimiffy.cn.biubiu.utils.AppUtils;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Description: api 接口
 * Created by kimiffy on 2019/2/25.
 */

public interface ApiService {

    /**
     * 正式服务器地址
     */
    String SERVER_ADDRESS_RELEASE = "https://www.wanandroid.com/";

    /**
     * 测试服务器地址
     */
    String SERVER_ADDRESS_DEBUG = "https://www.wanandroid.com/";

    /**
     * 服务器地址
     */
    String SERVER_ADDRESS = AppUtils.isDebug() ? SERVER_ADDRESS_DEBUG : SERVER_ADDRESS_RELEASE;



    /**
     * 登录
     */
    @POST("user/login")
    @FormUrlEncoded
    Observable<BaseBean<UserBean>> login(@Field("username") String username, @Field("password") String password);

    /**
     * 文章列表
     *
     * @param num 页码
     * @return
     */
    @GET("article/list/{page}/json")
    Observable<BaseBean<ArticleBean>> getArticleList(@Path("page") int num);

    /**
     * 获取 微信公众号列表
     * @return
     */
    @GET("/wxarticle/chapters/json")
    Observable<BaseBean<List<WxTitleBean>>> getWXList();


    /**
     *  获取 微信公众号详细信息列表数据
     * @param page 页码
     * @param id 公众号ID
     * @return
     */
    @GET("wxarticle/list/{id}/{page}/json")
    Observable<BaseBean<WxArticleListBean>> getWxArticleList( @Path("id")int id,@Path("page") int page );


    /**
     * 收藏文章
     * @param id
     */
    @POST("lg/collect/{id}/json")
    Observable<BaseBean> collectArticle(@Path("id") int id);

    /**
     *  取消收藏文章
     * @param id id
     */
    @POST("lg/uncollect_originId/{id}/json")
    Observable<BaseBean> unCollectArticle(@Path("id") int id);
}
