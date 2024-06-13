package id.jostudios.penielcommunity.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.provider.ContactsContract.Data
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import id.jostudios.penielcommunity.Enums.FeedType
import id.jostudios.penielcommunity.Helpers.AnimationHelper
import id.jostudios.penielcommunity.Helpers.DatabaseHelper
import id.jostudios.penielcommunity.Helpers.StorageHelper
import id.jostudios.penielcommunity.Models.FirebaseModels.FeedPostModel
import id.jostudios.penielcommunity.Models.FirebaseModels.UserModel
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

class FeedAdapter(private var dataList: List<FeedPostModel>): RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    private var fixedHeight: Int = 0;
    private lateinit var context: Context;
    private lateinit var viewGroup: ViewGroup;

    class FeedViewHolder(view: View): ViewHolder(view) {
        public var imgFeedType: ImageView;
        public var textCaptionFeed: TextView;

        public var imgProfileAuthor: ImageView;
        public var textAuthor: TextView;

        public var textThread: TextView;
        public var textShowMore: TextView;
        public var imgView: ImageView;

        public var imgBtnLike: ImageView;
        public var textLikeCounter: TextView;

        public var imgBtnComment: ImageView;
        public var textCommentCounter: TextView;

        public var textDate: TextView;

        init {
            imgFeedType = view.findViewById(R.id.img_feed_type);
            textCaptionFeed = view.findViewById(R.id.text_caption);

            imgProfileAuthor = view.findViewById(R.id.img_profile_author);
            textAuthor = view.findViewById(R.id.text_author);

            textThread = view.findViewById(R.id.text_thread);
            textShowMore = view.findViewById(R.id.text_show_more);
            imgView = view.findViewById(R.id.img_view);

            imgBtnLike = view.findViewById(R.id.img_btn_like);
            textLikeCounter = view.findViewById(R.id.text_like_counter);

            imgBtnComment = view.findViewById(R.id.img_btn_comment);
            textCommentCounter = view.findViewById(R.id.text_comment_counter);

            textDate = view.findViewById(R.id.text_date);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        var layout = LayoutInflater.from(parent.context);
        var inflate = layout.inflate(R.layout.template_feed_thread, parent, false);

        context = parent.context;
        viewGroup = parent;

        return FeedViewHolder(inflate!!);
    }

    override fun getItemCount(): Int {
        return dataList.size;
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val data = dataList[position];

        setStatics(data, holder);
        setDynamics(data, holder);
        setAuthorInfo(data, holder);
        setFeedExtras(data, holder);
    }

    private fun setStatics(data: FeedPostModel, holder: FeedViewHolder) {
        if (data.feedType == FeedType.Thread) {
            holder.imgFeedType.setImageResource(R.drawable.hashtag);
        } else if (data.feedType == FeedType.Photo || data.feedType == FeedType.Video) {
            holder.imgFeedType.setImageResource(R.drawable.camera);
        }

        holder.textCaptionFeed.text = data.caption;
        holder.textThread.text = data.thread;
        holder.textDate.text = setUploadDate(data.uploadTime);

        if (data.feedType == FeedType.Photo) {
            fetchImage(holder.imgView, data.photoUrl[0], object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    System.debug("Error! ${e}");
                    return false;
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (target == null) {
                        return true; }

                    target.getSize(SizeReadyCallback { width, height ->
                        var maxHeight = 1920;
                        var parsedHeight = resource.intrinsicHeight;

                        System.debug("Parsed height : ${parsedHeight}");

                        var targetHeight =
                            (width.toDouble() / resource.intrinsicWidth.toDouble() * parsedHeight.toDouble()).toInt();

                        if (holder.imgView.layoutParams.height != targetHeight) {
                            holder.imgView.layoutParams.height = targetHeight;
                            holder.imgView.requestLayout();
                        }
                    })

                    return false;
                }
            });
        }

        if (TextUtils.isEmpty(data.photoUrl[0])) {
            holder.imgView.setImageDrawable(null);
            holder.imgView.layoutParams.height = 0;
        }
    }

    private fun setDynamics(data: FeedPostModel, holder: FeedViewHolder) {
        GlobalScope.launch {
            var likes = DatabaseHelper.getFeedLikeByID(data.feedID);
            var comments = DatabaseHelper

            if (likes == null) {
                DatabaseHelper.addFeedLike(data.feedID);
                holder.textLikeCounter.text = "0";
                return@launch;
            }

            if (likes.userLikes.size > 0 && likes.userLikes[0] == 100.toLong()) {
                likes.userLikes.remove(100);
            }

            withContext(Dispatchers.Main) {
                var isLiked = likes.userLikes.find { it == GlobalState.currentUser?.id } != null;

                holder.textLikeCounter.text = likes.userLikes.size.toString();
                holder.textCommentCounter.text = "0";

                if (isLiked) {
                    holder.imgBtnLike.setImageResource(R.drawable.like_filled);
                } else {
                    holder.imgBtnLike.setImageResource(R.drawable.like);
                }

                holder.imgBtnLike.setOnClickListener {
                    isLiked = likes.userLikes.find { it == GlobalState.currentUser?.id } != null;

                    if (isLiked) {
                        likes.userLikes.remove(GlobalState.currentUser?.id!!);
                        AnimationHelper.animateView(context, holder.imgBtnLike, R.anim.anim_shrink_down);
                        AnimationHelper.animateView(context, holder.imgBtnLike, R.anim.anim_grow_up);
                        holder.imgBtnLike.setImageResource(R.drawable.like);
                        holder.textLikeCounter.text = likes.userLikes.size.toString();
                    } else {
                        likes.userLikes.add(GlobalState.currentUser?.id!!);
                        AnimationHelper.animateView(context, holder.imgBtnLike, R.anim.anim_shrink_down);
                        AnimationHelper.animateView(context, holder.imgBtnLike, R.anim.anim_grow_up);
                        holder.imgBtnLike.setImageResource(R.drawable.like_filled);
                        holder.textLikeCounter.text = likes.userLikes.size.toString();
                    }

                    GlobalScope.launch {
                        DatabaseHelper.postFeedLike(data.feedID, likes);
                    }
                }

                holder.imgBtnComment.setOnClickListener {
                    System.dialogMessageBox(context, "Info", "Fitur ini belum tersedia!");
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setFeedExtras(data: FeedPostModel, holder: FeedViewHolder) {
        if (holder.textThread.text.length > 50) {
            holder.textShowMore.isVisible = true;
            holder.textThread.text = data.thread.removeRange(50, data.thread.length) + "...";
        } else {
            holder.textShowMore.isVisible = false;
        }

        holder.textShowMore.setOnClickListener {
            it.isVisible = false;
            holder.textThread.text = data.thread;
        }
    }

    private fun setAuthorInfo(data: FeedPostModel, holder: FeedViewHolder) {
        GlobalScope.launch {
            val author = fetchAuthor(data.feedOwnerID);

            if (author == null) {
                withContext(Dispatchers.Main) {
                    holder.textAuthor.text = "user-not-found!";
                    holder.imgProfileAuthor.setImageResource(R.mipmap.blank_profile);
                }
                return@launch
            }

            val profileUrl = StorageHelper.getUserProfilePicture(author.profilePicture);

            withContext(Dispatchers.Main) {
                holder.textAuthor.text = author.displayName;
                System.debug("Author Picture : ${author.profilePicture}");
                fetchImage(holder.imgProfileAuthor, profileUrl.toString(), null);
            }
        }
    }

    private suspend fun fetchAuthor(id: Long): UserModel? {
        try {
            var author = DatabaseHelper.getUserByID(id);

            if (author == null) {
                System.debug("Author not found!");
                return null;
            }

            return author
        }
        catch (e: Exception)
        {
            System.debug("Error : ${e.message}");

            System.dialogMessageBox(context, "Error", e.message.toString());

            return null;
        }
    }

    private fun fetchImage(targetView: ImageView, url: String, listener: RequestListener<Drawable>?) {
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.time)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .fitCenter()
            .override(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        Glide.with(context)
            .load(url)
            .listener(listener)
            .apply(requestOptions)
            .into(targetView)
    }

    @SuppressLint("SimpleDateFormat")
    private fun setUploadDate(epochMs: Long): String {
        var formatter = SimpleDateFormat("d MMM yyyy");

        var uploadDate = Date(epochMs);
        var nowDate = Date();

        if (uploadDate.date == nowDate.date
            && uploadDate.month == nowDate.month
            && uploadDate.year == nowDate.year
        ) {
            return "Today";
        }

        return formatter.format(uploadDate);
    }
}