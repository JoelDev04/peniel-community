package id.jostudios.penielcommunity.Fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.jostudios.penielcommunity.databinding.FragmentCommentListDialogBinding
import id.jostudios.penielcommunity.databinding.FragmentCommentListDialogItemBinding

class CommentSheetFragment: BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCommentListDialogBinding;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentListDialogBinding.inflate(inflater, container, false);

        return binding.root;
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        dialog?.setOnShowListener {
            val d = it as BottomSheetDialog;
            val sheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            sheet.let {
                val behavior = BottomSheetBehavior.from(it!!);
                behavior.state = BottomSheetBehavior.STATE_EXPANDED;
            }
        }

        return super.onCreateDialog(savedInstanceState)
    }

    companion object {
        const val TAG = "CommentSheetFragment";
    }
}