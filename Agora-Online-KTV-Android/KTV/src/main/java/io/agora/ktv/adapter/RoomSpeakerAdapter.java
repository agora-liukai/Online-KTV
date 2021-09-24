package io.agora.ktv.adapter;

import android.content.Context;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agora.data.model.AgoraMember;
import com.agora.data.model.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.agora.baselibrary.base.BaseRecyclerViewAdapter;
import io.agora.ktv.R;
import io.agora.ktv.bean.MemberMusicModel;
import io.agora.ktv.databinding.KtvItemRoomSpeakerBinding;
import io.agora.ktv.manager.RoomManager;
import io.agora.rtc2.video.VideoCanvas;

import static io.agora.rtc2.Constants.RENDER_MODE_HIDDEN;

/**
 * 房间说话者列表
 *
 * @author chenhengfei@agora.io
 */
public class RoomSpeakerAdapter extends BaseRecyclerViewAdapter<AgoraMember, RoomSpeakerAdapter.ViewHolder> {

    public RoomSpeakerAdapter(@Nullable List<AgoraMember> datas, @Nullable Object listener) {
        super(datas, listener);
    }

    @Override
    public void addItem(@NonNull AgoraMember data) {
        if(data == null){
            return;
        }
        if (datas == null) {
            datas = new ArrayList<>();
        }

        int index = datas.indexOf(data);
        if (index < 0) {
            datas.add(data);
            notifyItemChanged(datas.size() - 1);
        } else {
            datas.set(index, data);
            notifyItemChanged(index);
        }
    }

    @Override
    public void deleteItem(@NonNull AgoraMember data) {
        if (datas == null || datas.isEmpty()) {
            return;
        }

        int index = datas.indexOf(data);
        if (0 <= index && index < datas.size()) {
            datas.remove(data);
        }

        //因为后面的需要往前面移动，所以直接进行全部更新
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    @Override
    public int getLayoutId() {
        return R.layout.ktv_item_room_speaker;
    }

    @Override
    public ViewHolder createHolder(View view, int viewType) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mDataBinding.tvName.setText(String.valueOf(position + 1));
        holder.mDataBinding.ivHead.setImageResource(R.mipmap.ktv_room_speaker_default);

        if (holder.mDataBinding.videoView.getChildCount() > 1) {
            holder.mDataBinding.videoView.removeViewAt(1);
        }
        AgoraMember item = getItemData(position);
        if (item == null) {
            return;
        }


        Context mContext = holder.itemView.getContext();


        SurfaceView surfaceView = RoomManager.Instance(mContext).getRtcEngine().CreateRendererView(mContext);
        if(holder.mDataBinding.videoView.getChildCount() <= 2){
            holder.mDataBinding.videoView.addView(surfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            if (RoomManager.Instance(mContext).getMine().getId() == item.getId())
            {
                RoomManager.Instance(mContext).getRtcEngine().setupLocalVideo(new VideoCanvas(surfaceView, RENDER_MODE_HIDDEN, 0));
            }
            else if (item.getStreamId() != 0){
                RoomManager.Instance(mContext).getRtcEngine().setupRemoteVideo(new VideoCanvas(surfaceView, RENDER_MODE_HIDDEN, item.getStreamId().intValue()));
            }
        }

        if (item.getRole() == AgoraMember.Role.Owner) {
            holder.mDataBinding.tvName.setText(mContext.getString(R.string.ktv_room_owner));
        }

//        User mUser = item.getUser();
//        if (mUser != null) {
//            Glide.with(holder.itemView)
//                    .load(mUser.getAvatarRes())
//                    .into(holder.mDataBinding.ivHead);
//        } else {
//            holder.mDataBinding.ivHead.setImageResource(R.mipmap.default_head);
//        }

        MemberMusicModel mMusicModel = RoomManager.Instance(mContext).getMusicModel();
        if (mMusicModel != null) {
            if (mMusicModel.getType() == MemberMusicModel.SingType.Single) {
                if (RoomManager.Instance(mContext).isSinger(item.getUserId())) {
                    holder.mDataBinding.tvName.setText(mContext.getString(R.string.ktv_room_sing1));
                }
            } else if (mMusicModel.getType() == MemberMusicModel.SingType.Chorus) {
                if (RoomManager.Instance(mContext).isSinger(item.getUserId())) {
                    holder.mDataBinding.tvName.setText(mContext.getString(R.string.ktv_room_sing1_1));
                }
            }
        }
    }

    class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder<KtvItemRoomSpeakerBinding> {

        public ViewHolder(View view) {
            super(view);
        }
    }
}
