package com.fphoenixcorneae.grouter

import androidx.fragment.app.Fragment
import com.fphoenixcorneae.grouter.annotation.Router

@Router(scheme = "deeplink", host = "grouter", path = "/third", description = "ThirdFragment")
class ThirdFragment : Fragment() {
}