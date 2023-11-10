package com.hane24.hoursarenotenough24

import com.google.gson.Gson
import com.hane24.hoursarenotenough24.network.GetAllTagPerMonthDto


object MonthLogsData {
    private val gson = Gson()
    val data202311 = gson.fromJson(jsonString202311, GetAllTagPerMonthDto::class.java)
    val data202310 = gson.fromJson(jsonString202310, GetAllTagPerMonthDto::class.java)
    val data202211 = gson.fromJson(jsonString202211, GetAllTagPerMonthDto::class.java)
}

const val jsonString202311 = """
    {
  "login": "jaewchoi",
  "profileImage": "https://cdn.intra.42.fr/users/8a9ee5d6fd23de4811bdb73c73cf8d5e/jaewchoi.jpg",
  "inOutLogs": [
    {
      "inTimeStamp": 1698925447,
      "outTimeStamp": 1698928450,
      "durationSecond": 3003
    },
    {
      "inTimeStamp": 1698923101,
      "outTimeStamp": 1698924982,
      "durationSecond": 1881
    },
    {
      "inTimeStamp": 1698922413,
      "outTimeStamp": 1698923048,
      "durationSecond": 635
    },
    {
      "inTimeStamp": 1698915178,
      "outTimeStamp": 1698922126,
      "durationSecond": 6948
    },
    {
      "inTimeStamp": 1698907081,
      "outTimeStamp": 1698914835,
      "durationSecond": 7754
    },
    {
      "inTimeStamp": 1698903787,
      "outTimeStamp": 1698906701,
      "durationSecond": 2914
    }
  ]
}
"""
const val jsonString202310 = """
    {
  "login": "jaewchoi",
  "profileImage": "https://cdn.intra.42.fr/users/8a9ee5d6fd23de4811bdb73c73cf8d5e/jaewchoi.jpg",
  "inOutLogs": [
    {
      "inTimeStamp": 1698747605,
      "outTimeStamp": 1698751279,
      "durationSecond": 3674
    },
    {
      "inTimeStamp": 1698744021,
      "outTimeStamp": 1698744403,
      "durationSecond": 382
    },
    {
      "inTimeStamp": 1698739035,
      "outTimeStamp": 1698743948,
      "durationSecond": 4913
    },
    {
      "inTimeStamp": 1698736050,
      "outTimeStamp": 1698737866,
      "durationSecond": 1816
    },
    {
      "inTimeStamp": 1698731267,
      "outTimeStamp": 1698735957,
      "durationSecond": 4690
    },
    {
      "inTimeStamp": 1698725732,
      "outTimeStamp": 1698730276,
      "durationSecond": 4544
    },
    {
      "inTimeStamp": 1698660355,
      "outTimeStamp": 1698666525,
      "durationSecond": 6170
    },
    {
      "inTimeStamp": 1698656589,
      "outTimeStamp": 1698660324,
      "durationSecond": 3735
    },
    {
      "inTimeStamp": 1698645177,
      "outTimeStamp": 1698656195,
      "durationSecond": 11018
    },
    {
      "inTimeStamp": 1698641409,
      "outTimeStamp": 1698644346,
      "durationSecond": 2937
    },
    {
      "inTimeStamp": 1698318290,
      "outTimeStamp": 1698320916,
      "durationSecond": 2626
    },
    {
      "inTimeStamp": 1698312205,
      "outTimeStamp": 1698315298,
      "durationSecond": 3093
    },
    {
      "inTimeStamp": 1698312109,
      "outTimeStamp": 1698312129,
      "durationSecond": 20
    },
    {
      "inTimeStamp": 1698305819,
      "outTimeStamp": 1698311597,
      "durationSecond": 5778
    },
    {
      "inTimeStamp": 1698305178,
      "outTimeStamp": 1698305684,
      "durationSecond": 506
    },
    {
      "inTimeStamp": 1698297004,
      "outTimeStamp": 1698304608,
      "durationSecond": 7604
    },
    {
      "inTimeStamp": 1698228247,
      "outTimeStamp": 1698232465,
      "durationSecond": 4218
    },
    {
      "inTimeStamp": 1698220593,
      "outTimeStamp": 1698224668,
      "durationSecond": 4075
    },
    {
      "inTimeStamp": 1698216487,
      "outTimeStamp": 1698220488,
      "durationSecond": 4001
    },
    {
      "inTimeStamp": 1698209400,
      "outTimeStamp": 1698215447,
      "durationSecond": 6047
    },
    {
      "inTimeStamp": 1698208780,
      "outTimeStamp": 1698208848,
      "durationSecond": 68
    },
    {
      "inTimeStamp": 1698138018,
      "outTimeStamp": 1698153385,
      "durationSecond": 15367
    },
    {
      "inTimeStamp": 1698119676,
      "outTimeStamp": 1698137612,
      "durationSecond": 17936
    },
    {
      "inTimeStamp": 1698118942,
      "outTimeStamp": 1698119317,
      "durationSecond": 375
    },
    {
      "inTimeStamp": 1698115562,
      "outTimeStamp": 1698116550,
      "durationSecond": 988
    },
    {
      "inTimeStamp": 1697804470,
      "outTimeStamp": 1697804590,
      "durationSecond": 120
    },
    {
      "inTimeStamp": 1697795789,
      "outTimeStamp": 1697803983,
      "durationSecond": 8194
    },
    {
      "inTimeStamp": 1697788167,
      "outTimeStamp": 1697793915,
      "durationSecond": 5748
    },
    {
      "inTimeStamp": 1697784040,
      "outTimeStamp": 1697788082,
      "durationSecond": 4042
    },
    {
      "inTimeStamp": 1697776584,
      "outTimeStamp": 1697783228,
      "durationSecond": 6644
    },
    {
      "inTimeStamp": 1697538429,
      "outTimeStamp": 1697541543,
      "durationSecond": 3114
    },
    {
      "inTimeStamp": 1697534758,
      "outTimeStamp": 1697538074,
      "durationSecond": 3316
    },
    {
      "inTimeStamp": 1697527307,
      "outTimeStamp": 1697534649,
      "durationSecond": 7342
    },
    {
      "inTimeStamp": 1697525592,
      "outTimeStamp": 1697527063,
      "durationSecond": 1471
    },
    {
      "inTimeStamp": 1697521525,
      "outTimeStamp": 1697524548,
      "durationSecond": 3023
    },
    {
      "inTimeStamp": 1697520570,
      "outTimeStamp": 1697520646,
      "durationSecond": 76
    },
    {
      "inTimeStamp": 1697453416,
      "outTimeStamp": 1697457462,
      "durationSecond": 4046
    },
    {
      "inTimeStamp": 1697449623,
      "outTimeStamp": 1697450072,
      "durationSecond": 449
    },
    {
      "inTimeStamp": 1697446239,
      "outTimeStamp": 1697449544,
      "durationSecond": 3305
    },
    {
      "inTimeStamp": 1697440658,
      "outTimeStamp": 1697446163,
      "durationSecond": 5505
    },
    {
      "inTimeStamp": 1697432294,
      "outTimeStamp": 1697440000,
      "durationSecond": 7706
    },
    {
      "inTimeStamp": 1697201231,
      "outTimeStamp": 1697201247,
      "durationSecond": 16
    },
    {
      "inTimeStamp": 1697200329,
      "outTimeStamp": 1697201201,
      "durationSecond": 872
    },
    {
      "inTimeStamp": 1697199714,
      "outTimeStamp": 1697199849,
      "durationSecond": 135
    },
    {
      "inTimeStamp": 1697187845,
      "outTimeStamp": 1697199262,
      "durationSecond": 11417
    },
    {
      "inTimeStamp": 1697186502,
      "outTimeStamp": 1697186589,
      "durationSecond": 87
    },
    {
      "inTimeStamp": 1697177974,
      "outTimeStamp": 1697182864,
      "durationSecond": 4890
    },
    {
      "inTimeStamp": 1697170729,
      "outTimeStamp": 1697177188,
      "durationSecond": 6459
    },
    {
      "inTimeStamp": 1697168315,
      "outTimeStamp": 1697169747,
      "durationSecond": 1432
    },
    {
      "inTimeStamp": 1697094568,
      "outTimeStamp": 1697099255,
      "durationSecond": 4687
    },
    {
      "inTimeStamp": 1697085275,
      "outTimeStamp": 1697094229,
      "durationSecond": 8954
    },
    {
      "inTimeStamp": 1697084835,
      "outTimeStamp": 1697084867,
      "durationSecond": 32
    },
    {
      "inTimeStamp": 1697075699,
      "outTimeStamp": null,
      "durationSecond": null
    },
    {
      "inTimeStamp": 1697018806,
      "outTimeStamp": 1697023757,
      "durationSecond": 4951
    },
    {
      "inTimeStamp": 1697012243,
      "outTimeStamp": 1697018459,
      "durationSecond": 6216
    },
    {
      "inTimeStamp": 1697000662,
      "outTimeStamp": 1697011179,
      "durationSecond": 10517
    },
    {
      "inTimeStamp": 1696999182,
      "outTimeStamp": 1697000360,
      "durationSecond": 1178
    },
    {
      "inTimeStamp": 1696993657,
      "outTimeStamp": 1696997276,
      "durationSecond": 3619
    },
    {
      "inTimeStamp": 1696992826,
      "outTimeStamp": 1696993344,
      "durationSecond": 518
    },
    {
      "inTimeStamp": 1696938830,
      "outTimeStamp": 1696938847,
      "durationSecond": 17
    },
    {
      "inTimeStamp": 1696932036,
      "outTimeStamp": 1696938795,
      "durationSecond": 6759
    },
    {
      "inTimeStamp": 1696922100,
      "outTimeStamp": 1696928959,
      "durationSecond": 6859
    },
    {
      "inTimeStamp": 1696915066,
      "outTimeStamp": 1696921568,
      "durationSecond": 6502
    },
    {
      "inTimeStamp": 1696588987,
      "outTimeStamp": 1696589340,
      "durationSecond": 353
    },
    {
      "inTimeStamp": 1696579956,
      "outTimeStamp": 1696588387,
      "durationSecond": 8431
    },
    {
      "inTimeStamp": 1696574436,
      "outTimeStamp": 1696579317,
      "durationSecond": 4881
    },
    {
      "inTimeStamp": 1696569093,
      "outTimeStamp": 1696573937,
      "durationSecond": 4844
    },
    {
      "inTimeStamp": 1696568368,
      "outTimeStamp": 1696568467,
      "durationSecond": 99
    },
    {
      "inTimeStamp": 1696565247,
      "outTimeStamp": 1696565282,
      "durationSecond": 35
    }
  ]
}
"""

const val jsonString202211 = """
    {
  "login": "jaewchoi",
  "profileImage": "https://cdn.intra.42.fr/users/8a9ee5d6fd23de4811bdb73c73cf8d5e/jaewchoi.jpg",
  "inOutLogs": [
    {
      "inTimeStamp": 1669803707,
      "outTimeStamp": 1669820399,
      "durationSecond": 16692
    },
    {
      "inTimeStamp": 1669798185,
      "outTimeStamp": 1669803603,
      "durationSecond": 5418
    },
    {
      "inTimeStamp": 1669794912,
      "outTimeStamp": 1669798098,
      "durationSecond": 3186
    },
    {
      "inTimeStamp": 1669789516,
      "outTimeStamp": 1669794823,
      "durationSecond": 5307
    },
    {
      "inTimeStamp": 1669788576,
      "outTimeStamp": 1669788602,
      "durationSecond": 26
    },
    {
      "inTimeStamp": 1669787379,
      "outTimeStamp": 1669788566,
      "durationSecond": 1187
    },
    {
      "inTimeStamp": 1669635521,
      "outTimeStamp": 1669637012,
      "durationSecond": 1491
    },
    {
      "inTimeStamp": 1669630434,
      "outTimeStamp": 1669635264,
      "durationSecond": 4830
    },
    {
      "inTimeStamp": 1669626336,
      "outTimeStamp": 1669630144,
      "durationSecond": 3808
    },
    {
      "inTimeStamp": 1669615154,
      "outTimeStamp": 1669626111,
      "durationSecond": 10957
    },
    {
      "inTimeStamp": 1669607229,
      "outTimeStamp": 1669613348,
      "durationSecond": 6119
    },
    {
      "inTimeStamp": 1669519982,
      "outTimeStamp": 1669521512,
      "durationSecond": 1530
    },
    {
      "inTimeStamp": 1669500846,
      "outTimeStamp": 1669519885,
      "durationSecond": 19039
    },
    {
      "inTimeStamp": 1669491880,
      "outTimeStamp": 1669498316,
      "durationSecond": 6436
    },
    {
      "inTimeStamp": 1669481005,
      "outTimeStamp": 1669491265,
      "durationSecond": 10260
    },
    {
      "inTimeStamp": 1669480304,
      "outTimeStamp": 1669480949,
      "durationSecond": 645
    },
    {
      "inTimeStamp": 1669477794,
      "outTimeStamp": 1669479536,
      "durationSecond": 1742
    },
    {
      "inTimeStamp": 1669475305,
      "outTimeStamp": 1669477728,
      "durationSecond": 2423
    },
    {
      "inTimeStamp": 1669474800,
      "outTimeStamp": 1669475209,
      "durationSecond": 409
    },
    {
      "inTimeStamp": 1669470004,
      "outTimeStamp": 1669474799,
      "durationSecond": 4795
    },
    {
      "inTimeStamp": 1669469353,
      "outTimeStamp": 1669469431,
      "durationSecond": 78
    },
    {
      "inTimeStamp": 1669459809,
      "outTimeStamp": 1669469337,
      "durationSecond": 9528
    },
    {
      "inTimeStamp": 1669459750,
      "outTimeStamp": 1669459797,
      "durationSecond": 47
    },
    {
      "inTimeStamp": 1669319615,
      "outTimeStamp": 1669331552,
      "durationSecond": 11937
    },
    {
      "inTimeStamp": 1669313483,
      "outTimeStamp": 1669319207,
      "durationSecond": 5724
    },
    {
      "inTimeStamp": 1669303910,
      "outTimeStamp": 1669313112,
      "durationSecond": 9202
    },
    {
      "inTimeStamp": 1669302000,
      "outTimeStamp": 1669303489,
      "durationSecond": 1489
    },
    {
      "inTimeStamp": 1669294122,
      "outTimeStamp": 1669301999,
      "durationSecond": 7877
    },
    {
      "inTimeStamp": 1669288681,
      "outTimeStamp": 1669293655,
      "durationSecond": 4974
    },
    {
      "inTimeStamp": 1669284063,
      "outTimeStamp": 1669288361,
      "durationSecond": 4298
    },
    {
      "inTimeStamp": 1669203283,
      "outTimeStamp": 1669212391,
      "durationSecond": 9108
    },
    {
      "inTimeStamp": 1669195170,
      "outTimeStamp": 1669203188,
      "durationSecond": 8018
    },
    {
      "inTimeStamp": 1669188991,
      "outTimeStamp": 1669192189,
      "durationSecond": 3198
    },
    {
      "inTimeStamp": 1669182125,
      "outTimeStamp": 1669188309,
      "durationSecond": 6184
    },
    {
      "inTimeStamp": 1669176442,
      "outTimeStamp": 1669181490,
      "durationSecond": 5048
    },
    {
      "inTimeStamp": 1669114144,
      "outTimeStamp": 1669118444,
      "durationSecond": 4300
    },
    {
      "inTimeStamp": 1669113356,
      "outTimeStamp": 1669114133,
      "durationSecond": 777
    },
    {
      "inTimeStamp": null,
      "outTimeStamp": 1669113351,
      "durationSecond": null
    },
    {
      "inTimeStamp": 1669103031,
      "outTimeStamp": 1669108665,
      "durationSecond": 5634
    },
    {
      "inTimeStamp": 1669096248,
      "outTimeStamp": 1669102285,
      "durationSecond": 6037
    },
    {
      "inTimeStamp": 1669089849,
      "outTimeStamp": 1669096165,
      "durationSecond": 6316
    },
    {
      "inTimeStamp": 1669089212,
      "outTimeStamp": 1669089286,
      "durationSecond": 74
    },
    {
      "inTimeStamp": 1669089180,
      "outTimeStamp": 1669089201,
      "durationSecond": 21
    },
    {
      "inTimeStamp": 1669080950,
      "outTimeStamp": 1669086259,
      "durationSecond": 5309
    },
    {
      "inTimeStamp": 1669070634,
      "outTimeStamp": 1669080069,
      "durationSecond": 9435
    },
    {
      "inTimeStamp": 1669010874,
      "outTimeStamp": 1669010985,
      "durationSecond": 111
    },
    {
      "inTimeStamp": 1669010807,
      "outTimeStamp": 1669010863,
      "durationSecond": 56
    },
    {
      "inTimeStamp": 1669010740,
      "outTimeStamp": 1669010795,
      "durationSecond": 55
    },
    {
      "inTimeStamp": 1669006636,
      "outTimeStamp": 1669010647,
      "durationSecond": 4011
    },
    {
      "inTimeStamp": 1669004923,
      "outTimeStamp": 1669006624,
      "durationSecond": 1701
    },
    {
      "inTimeStamp": 1669001292,
      "outTimeStamp": 1669004840,
      "durationSecond": 3548
    },
    {
      "inTimeStamp": 1668998807,
      "outTimeStamp": 1669001211,
      "durationSecond": 2404
    },
    {
      "inTimeStamp": 1668998609,
      "outTimeStamp": 1668998792,
      "durationSecond": 183
    },
    {
      "inTimeStamp": 1668993330,
      "outTimeStamp": 1668998168,
      "durationSecond": 4838
    },
    {
      "inTimeStamp": 1668990219,
      "outTimeStamp": 1668993238,
      "durationSecond": 3019
    },
    {
      "inTimeStamp": 1668989281,
      "outTimeStamp": 1668989322,
      "durationSecond": 41
    },
    {
      "inTimeStamp": 1668986566,
      "outTimeStamp": 1668986626,
      "durationSecond": 60
    },
    {
      "inTimeStamp": 1668974517,
      "outTimeStamp": 1668985895,
      "durationSecond": 11378
    },
    {
      "inTimeStamp": 1668972594,
      "outTimeStamp": 1668974422,
      "durationSecond": 1828
    },
    {
      "inTimeStamp": 1668970793,
      "outTimeStamp": 1668972516,
      "durationSecond": 1723
    },
    {
      "inTimeStamp": 1668970060,
      "outTimeStamp": 1668970109,
      "durationSecond": 49
    },
    {
      "inTimeStamp": 1668966922,
      "outTimeStamp": 1668969976,
      "durationSecond": 3054
    },
    {
      "inTimeStamp": 1668964724,
      "outTimeStamp": 1668966820,
      "durationSecond": 2096
    },
    {
      "inTimeStamp": 1668963835,
      "outTimeStamp": 1668964541,
      "durationSecond": 706
    },
    {
      "inTimeStamp": 1668963163,
      "outTimeStamp": 1668963756,
      "durationSecond": 593
    },
    {
      "inTimeStamp": 1668962779,
      "outTimeStamp": 1668962908,
      "durationSecond": 129
    },
    {
      "inTimeStamp": 1668959756,
      "outTimeStamp": 1668962605,
      "durationSecond": 2849
    },
    {
      "inTimeStamp": 1668959331,
      "outTimeStamp": 1668959461,
      "durationSecond": 130
    },
    {
      "inTimeStamp": 1668956400,
      "outTimeStamp": 1668958684,
      "durationSecond": 2284
    },
    {
      "inTimeStamp": 1668950739,
      "outTimeStamp": 1668956399,
      "durationSecond": 5660
    },
    {
      "inTimeStamp": 1668945451,
      "outTimeStamp": 1668949753,
      "durationSecond": 4302
    },
    {
      "inTimeStamp": 1668718147,
      "outTimeStamp": 1668728900,
      "durationSecond": 10753
    },
    {
      "inTimeStamp": 1668716796,
      "outTimeStamp": 1668717880,
      "durationSecond": 1084
    },
    {
      "inTimeStamp": 1668713945,
      "outTimeStamp": 1668716172,
      "durationSecond": 2227
    },
    {
      "inTimeStamp": 1668711939,
      "outTimeStamp": 1668713870,
      "durationSecond": 1931
    },
    {
      "inTimeStamp": 1668707663,
      "outTimeStamp": 1668711852,
      "durationSecond": 4189
    },
    {
      "inTimeStamp": 1668697200,
      "outTimeStamp": 1668706605,
      "durationSecond": 9405
    },
    {
      "inTimeStamp": 1668695408,
      "outTimeStamp": 1668697199,
      "durationSecond": 1791
    },
    {
      "inTimeStamp": 1668687769,
      "outTimeStamp": 1668694718,
      "durationSecond": 6949
    },
    {
      "inTimeStamp": 1668687479,
      "outTimeStamp": 1668687758,
      "durationSecond": 279
    },
    {
      "inTimeStamp": 1668683948,
      "outTimeStamp": 1668687379,
      "durationSecond": 3431
    },
    {
      "inTimeStamp": 1668677911,
      "outTimeStamp": 1668682283,
      "durationSecond": 4372
    },
    {
      "inTimeStamp": 1668677768,
      "outTimeStamp": 1668677813,
      "durationSecond": 45
    },
    {
      "inTimeStamp": 1668676494,
      "outTimeStamp": 1668676695,
      "durationSecond": 201
    },
    {
      "inTimeStamp": 1668675579,
      "outTimeStamp": 1668676481,
      "durationSecond": 902
    },
    {
      "inTimeStamp": 1668673971,
      "outTimeStamp": 1668675568,
      "durationSecond": 1597
    },
    {
      "inTimeStamp": 1668610800,
      "outTimeStamp": 1668612141,
      "durationSecond": 1341
    },
    {
      "inTimeStamp": 1668607110,
      "outTimeStamp": 1668610799,
      "durationSecond": 3689
    },
    {
      "inTimeStamp": 1668601369,
      "outTimeStamp": 1668607018,
      "durationSecond": 5649
    },
    {
      "inTimeStamp": 1668593131,
      "outTimeStamp": 1668600695,
      "durationSecond": 7564
    },
    {
      "inTimeStamp": 1668589985,
      "outTimeStamp": 1668593046,
      "durationSecond": 3061
    },
    {
      "inTimeStamp": 1668587186,
      "outTimeStamp": 1668587203,
      "durationSecond": 17
    },
    {
      "inTimeStamp": 1668583911,
      "outTimeStamp": 1668587050,
      "durationSecond": 3139
    },
    {
      "inTimeStamp": 1668504525,
      "outTimeStamp": 1668520525,
      "durationSecond": 16000
    },
    {
      "inTimeStamp": 1668492845,
      "outTimeStamp": 1668502873,
      "durationSecond": 10028
    },
    {
      "inTimeStamp": 1668421090,
      "outTimeStamp": 1668427709,
      "durationSecond": 6619
    },
    {
      "inTimeStamp": 1668407617,
      "outTimeStamp": 1668420296,
      "durationSecond": 12679
    },
    {
      "inTimeStamp": 1668401301,
      "outTimeStamp": 1668404615,
      "durationSecond": 3314
    },
    {
      "inTimeStamp": 1668152049,
      "outTimeStamp": 1668165683,
      "durationSecond": 13634
    },
    {
      "inTimeStamp": 1668148573,
      "outTimeStamp": 1668151335,
      "durationSecond": 2762
    },
    {
      "inTimeStamp": 1668082330,
      "outTimeStamp": 1668087375,
      "durationSecond": 5045
    },
    {
      "inTimeStamp": 1668075568,
      "outTimeStamp": 1668081899,
      "durationSecond": 6331
    },
    {
      "inTimeStamp": 1668061251,
      "outTimeStamp": null,
      "durationSecond": null
    },
    {
      "inTimeStamp": 1668055643,
      "outTimeStamp": 1668060228,
      "durationSecond": 4585
    },
    {
      "inTimeStamp": 1667900221,
      "outTimeStamp": 1667915153,
      "durationSecond": 14932
    },
    {
      "inTimeStamp": 1667887252,
      "outTimeStamp": 1667897817,
      "durationSecond": 10565
    },
    {
      "inTimeStamp": 1667881291,
      "outTimeStamp": 1667886553,
      "durationSecond": 5262
    },
    {
      "inTimeStamp": 1667812079,
      "outTimeStamp": 1667825357,
      "durationSecond": 13278
    },
    {
      "inTimeStamp": 1667805423,
      "outTimeStamp": 1667808696,
      "durationSecond": 3273
    },
    {
      "inTimeStamp": 1667796310,
      "outTimeStamp": 1667804870,
      "durationSecond": 8560
    },
    {
      "inTimeStamp": 1667556905,
      "outTimeStamp": 1667559041,
      "durationSecond": 2136
    },
    {
      "inTimeStamp": 1667545478,
      "outTimeStamp": 1667556619,
      "durationSecond": 11141
    },
    {
      "inTimeStamp": 1667539390,
      "outTimeStamp": 1667545157,
      "durationSecond": 5767
    },
    {
      "inTimeStamp": 1667468673,
      "outTimeStamp": 1667471188,
      "durationSecond": 2515
    },
    {
      "inTimeStamp": 1667462879,
      "outTimeStamp": 1667465783,
      "durationSecond": 2904
    },
    {
      "inTimeStamp": 1667459186,
      "outTimeStamp": 1667462526,
      "durationSecond": 3340
    },
    {
      "inTimeStamp": 1667454287,
      "outTimeStamp": 1667458872,
      "durationSecond": 4585
    },
    {
      "inTimeStamp": 1667448758,
      "outTimeStamp": 1667453823,
      "durationSecond": 5065
    },
    {
      "inTimeStamp": 1667442652,
      "outTimeStamp": 1667445761,
      "durationSecond": 3109
    },
    {
      "inTimeStamp": 1667439723,
      "outTimeStamp": 1667442346,
      "durationSecond": 2623
    },
    {
      "inTimeStamp": 1667431428,
      "outTimeStamp": 1667439349,
      "durationSecond": 7921
    },
    {
      "inTimeStamp": 1667390253,
      "outTimeStamp": 1667398104,
      "durationSecond": 7851
    },
    {
      "inTimeStamp": 1667384112,
      "outTimeStamp": 1667389925,
      "durationSecond": 5813
    },
    {
      "inTimeStamp": 1667374154,
      "outTimeStamp": 1667383815,
      "durationSecond": 9661
    },
    {
      "inTimeStamp": 1667366424,
      "outTimeStamp": 1667373864,
      "durationSecond": 7440
    },
    {
      "inTimeStamp": 1667361724,
      "outTimeStamp": 1667365986,
      "durationSecond": 4262
    },
    {
      "inTimeStamp": 1667299691,
      "outTimeStamp": 1667310818,
      "durationSecond": 11127
    },
    {
      "inTimeStamp": 1667298972,
      "outTimeStamp": 1667299379,
      "durationSecond": 407
    },
    {
      "inTimeStamp": 1667291413,
      "outTimeStamp": 1667296338,
      "durationSecond": 4925
    },
    {
      "inTimeStamp": 1667286714,
      "outTimeStamp": 1667291115,
      "durationSecond": 4401
    },
    {
      "inTimeStamp": 1667280739,
      "outTimeStamp": 1667286425,
      "durationSecond": 5686
    },
    {
      "inTimeStamp": 1667275260,
      "outTimeStamp": 1667280490,
      "durationSecond": 5230
    }
  ]
}
"""
