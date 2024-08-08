
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Mr.M
 * @version 1.0
 * @description Scan code payment test
 * @date 2023/2/25 16:17
 */

@Controller
public class PayTestController {
    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    //Order interface test
    @RequestMapping("/alipaytest")
    public void doPost(HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) throws ServletException, IOException, AlipayApiException {

        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, AlipayConfig.FORMAT,
                AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//Create a request corresponding to the API
        //alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        //Set the callback and notification addresses in the public parameters
        alipayRequest.setNotifyUrl("http://tjxt-user-t.itheima.net/xuecheng/orders/paynotify");
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":\"202303200101020011\"," +
                "    \"total_amount\":0.1," +
                "    \"subject\":\"Iphone14 16G\"," +
                "    \"product_code\":\"QUICK_WAP_WAY\"" +
                "  }");//Fill in business parameters
        String form = alipayClient.pageExecute(alipayRequest).getBody(); //Call SDK to generate form
        httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        httpResponse.getWriter().write(form);//Output the complete form HTML directly to the page
        httpResponse.getWriter().flush();
    }

    //Payment result notification
    @PostMapping("/paynotifytest")
    public void paynotify(HttpServletRequest request,HttpServletResponse response) throws IOException, AlipayApiException {

        //Get the feedback information from Alipay POST
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //This code is used when garbled characters appear. If mysign and sign are not equal, this code can also be used to convert
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        //Get the notification return parameters of Alipay. Please refer to the page jump synchronization notification
        // parameter list in the technical documentation (the following is for reference only)

        //Get the notification return parameters of Alipay. Please refer to the page jump synchronization notification
        //parameter list in the technical documentation (the above is for reference only)
        //Calculate the notification verification result
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        boolean verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");

        if(verify_result){//Verification Success
            //////////////////////////////////////////////////////////////////////////////////////////
            //Please add the merchant's business logic program code here
            //Merchant order number

            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //Alipay transaction number

            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //trading status
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

            if(trade_status.equals("TRADE_FINISHED")){
                /*Determine whether the order has been processed on the merchant website
                If it has not been processed, find the details of the order in the order system of the merchant
                website according to the order number (out_trade_no) and execute the merchant's business procedures
                Please make sure that the total_fee and seller_id at the time of the request are consistent with the
                total_fee and seller_id obtained at the time of notification
                If it has been processed, do not execute the merchant's business procedures

                Note:
                If the signed agreement is a refundable agreement, the Alipay system sends the transaction status
                notification after the refund date exceeds the refundable period (such as three months of refund)
                If the signed agreement is not a refundable agreement, then the Alipay system sends the transaction
                status notification after the payment is completed.*/
            } else if (trade_status.equals("TRADE_SUCCESS")){
                //Determine whether the order has been processed on the merchant website
                //If it has not been processed, find out the details of the order in the order system of the merchant
                //website according to the order number (out_trade_no) and execute the merchant's business procedures
                //Please make sure that the total_fee and seller_id at the time of the request are consistent with the
                //total_fee and seller_id obtained at the time of notification
                //If it has been processed, do not execute the merchant's business procedures
                System.out.println(trade_status);
                //Note:
                //If the signed agreement is a refundable agreement, then after the payment is completed, the Alipay
                //system will send a transaction status notification.
            }

            response.getWriter().write("success");

            //////////////////////////////////////////////////////////////////////////////////////////
        }else{//verification failed
            response.getWriter().write("fail");
        }


    }

}
