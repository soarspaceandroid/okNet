package android.soar.com.testapplication.net

import com.soar.soarlibrary.net.RequestPartsInter

/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/9
 *※ Time : 17:45
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.android.http
 *----------------------------------------------------
 * library 获取参数中介 此类包名位置不能变
 */
class RequestPartsIntermediary : RequestPartsInter {


    override fun getChannel(): String {
        return "yonglibao"
    }


    override fun getToken(): String {
        return ""
    }

    override fun getUrl(): String {
        return DomainEnum.PRODUCT.url
    }


    companion object {
        fun getHybridOnLineHost(currentUrl: String): String {
            return if (currentUrl.startsWith(DomainEnum.PRODUCT.url)) {
                "release"
            }else if (currentUrl.startsWith(DomainEnum.PRE_TEST.url)) {
                "gray"
            } else {
                "test"
            }
        }
    }

}