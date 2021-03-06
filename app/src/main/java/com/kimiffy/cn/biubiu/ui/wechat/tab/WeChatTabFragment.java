package com.kimiffy.cn.biubiu.ui.wechat.tab;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kimiffy.cn.biubiu.R;
import com.kimiffy.cn.biubiu.base.LazyMVPFragment;
import com.kimiffy.cn.biubiu.bean.WxArticleListBean;
import com.kimiffy.cn.biubiu.constant.Key;
import com.kimiffy.cn.biubiu.utils.ToastUtil;
import com.kimiffy.cn.biubiu.utils.aop.FilterType;
import com.kimiffy.cn.biubiu.utils.aop.annotation.LoginFilter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Description:公众号文章列表
 * Created by kimiffy on 2019/5/3.
 */

public class WeChatTabFragment extends LazyMVPFragment<WeChatTabPresenter> implements WeChatTabContract.View {


    @BindView(R.id.rlv_article)
    RecyclerView mRlvArticle;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mSrlRefresh;
    private int id;
    private List<WxArticleListBean.DatasBean> mList;
    private WxArticleListAdapter mAdapter;

    public static WeChatTabFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(Key.ARGUMENT_ID, id);
        WeChatTabFragment fragment = new WeChatTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected WeChatTabPresenter createPresenter() {
        return new WeChatTabPresenter(this);
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_we_chat_tab;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (null != arguments) {
            id = arguments.getInt(Key.ARGUMENT_ID, -1);
        }
        mList = new ArrayList<>();
    }

    @Override
    protected void initUI() {
        mSrlRefresh.setColorSchemeColors(getResources().getColor(R.color.md_blue_A200), getResources().getColor(R.color.md_blue_A400));
        mAdapter = new WxArticleListAdapter(mActivity, R.layout.item_rlv_wx_article, mList);
        mRlvArticle.setLayoutManager(new LinearLayoutManager(getBindActivity()));
        mRlvArticle.addItemDecoration(new DividerItemDecoration(getBindActivity(), LinearLayoutManager.VERTICAL));
        mRlvArticle.setAdapter(mAdapter);
    }


    @Override
    protected void setListener() {
        mSrlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.reFresh();
            }
        });

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMore();
            }
        }, mRlvArticle);

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                WxArticleListBean.DatasBean item = (WxArticleListBean.DatasBean) baseQuickAdapter.getData().get(i);
                switch (view.getId()) {
                    case R.id.iv_collect:
                        collectClick((ImageView) view, item,i);
                        break;
                        default:
                            break;
                }
            }
        });

        mStateView.getStateViewImpl().setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast("点击重试!");
            }
        });
    }

    /**
     * 收藏/取消收藏
     */
    @LoginFilter(FilterType.JUMP)
    private void collectClick(ImageView view, WxArticleListBean.DatasBean item,int position) {
        boolean collect = item.isCollect();
        if (collect) {
            view.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_collect_normal));
            view.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.collect));
            mPresenter.unCollect(item.getId(),position);
        } else {
            view.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_collect));
            view.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.collect));
            mPresenter.doCollect(item.getId(),position);
        }
    }

    @Override
    protected void onVisibleStateChange(boolean isVisible) {
        if (!isVisible) {
            mSrlRefresh.setRefreshing(false);
        }
    }

    @Override
    protected void onLazyLoad() {
        mSrlRefresh.setRefreshing(true);
        mPresenter.firstFresh(id);
    }


    @Override
    protected View getStateViewRootView() {
        return mRlvArticle;
    }

    @Override
    public void getDataSuccess(WxArticleListBean bean, boolean isRefresh) {
        List<WxArticleListBean.DatasBean> datas = bean.getDatas();
        if (isRefresh) {
            mList = datas;
            mAdapter.replaceData(mList);
        } else {
            if (!datas.isEmpty()) {
                mAdapter.addData(datas);
                mAdapter.loadMoreComplete();
            } else {
                mAdapter.loadMoreEnd();
            }
        }
        mSrlRefresh.setRefreshing(false);
    }

    @Override
    public void getDataFail(String info) {
        mSrlRefresh.setRefreshing(false);
    }



    @Override
    public void collectSuccess(int position) {
        mAdapter.getData().get(position).setCollect(true);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void collectFail(int position, String msg) {
        mAdapter.getData().get(position).setCollect(false);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void unCollectSuccess(int position) {
        mAdapter.getData().get(position).setCollect(false);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void unCollectFail(int position, String msg) {
        mAdapter.getData().get(position).setCollect(true);
        mAdapter.notifyItemChanged(position);
    }


}
