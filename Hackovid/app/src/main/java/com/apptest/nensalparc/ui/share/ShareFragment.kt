package com.apptest.nensalparc.ui.share

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.apptest.nensalparc.AreaInfoModel
import com.apptest.nensalparc.R
import com.apptest.nensalparc.ui.send.PlaceActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_share.*

class ShareFragment(var info: AreaInfoModel) : Fragment() {

    private lateinit var shareViewModel: ShareViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        shareViewModel =
            ViewModelProviders.of(this).get(ShareViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_share, container, false)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_name.text = info.name;
        text_address.text = info.address;

        Picasso.get().load(info.imageUrl).into(image_preview);
        button_make_reservation.setOnClickListener({
            val intent = Intent(context, PlaceActivity::class.java).apply {
                this.putExtra("place", info);
            }
            startActivity(intent);
        })
    }
}