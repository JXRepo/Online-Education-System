
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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
 * @description Order related interfaces
 * @date 2023/2/26 10:09
 */
@Api(value = "Order payment interface", tags = "Order payment interface")
@Slf4j
@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @ApiOperation("Generate payment QR code")
    @PostMapping("/generatepaycode")
    @ResponseBody
    public PayRecordDto generatePayCode(@RequestBody AddOrderDto addOrderDto) {

        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String userId = user.getId();

        //Call the service to insert order information, insert payment records, and generate payment QR code
        PayRecordDto payRecordDto = orderService.createOrder(userId, addOrderDto);

        return payRecordDto;
    }

    @ApiOperation("Scan code to place an order")
    @GetMapping("/requestpay")
    public void requestpay(String payNo, HttpServletResponse httpResponse) throws IOException, AlipayApiException {

        //The payment record number is passed in to determine whether the payment record number exists
        XcPayRecord payRecordByPayno = orderService.getPayRecordByPayno(payNo);
        if(payRecordByPayno == null){
            XueChengPlusException.cast("Payment record does not exist");
        }
        //Payment results
        String status = payRecordByPayno.getStatus();
        if ("601002".equals(status)) {

            XueChengPlusException.cast("Already paid, no need to pay again");
        }

        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//Create a request corresponding to the API
        //alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        //Set the callback and notification addresses in the public parameters
        alipayRequest.setNotifyUrl("http://tjxt-user-t.itheima.net/xuecheng/orders/paynotify");
        alipayRequest.setBizContent("{" +
                "    \"out_trade_no\":\""+payNo+"\"," +
                "    \"total_amount\":"+payRecordByPayno.getTotalPrice()+"," +
                "    \"subject\":\""+payRecordByPayno.getOrderName()+"\"," +
                "    \"product_code\":\"QUICK_WAP_WAY\"" +
                "  }");//Fill in business parameters
        String form = alipayClient.pageExecute(alipayRequest).getBody(); //Call SDK to request Alipay to place an order
        httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        httpResponse.getWriter().write(form);//Output the complete form HTML directly to the page
        httpResponse.getWriter().flush();


    }

    @ApiOperation("Query payment results")
    @GetMapping("/payresult")
    @ResponseBody
    public PayRecordDto payresult(String payNo) throws IOException {
        //Query payment results
        PayRecordDto payRecordDto = orderService.queryPayResult(payNo);
        return payRecordDto;

    }

    //Receive Alipay notifications
    @PostMapping("/paynotify")
    public void paynotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {

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
            params.put(name, valueStr);
        }
        boolean verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");

        if(verify_result){//Verification Success
            //Merchant order number
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //Alipay transaction number
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //Amount of the transaction
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");
            //trading status
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
            if (trade_status.equals("TRADE_SUCCESS")){
                //Update the payment status of the payment record table to success and the status of the order table to success
                PayStatusDto payStatusDto = new PayStatusDto();
                payStatusDto.setTrade_status(trade_status);
                payStatusDto.setTrade_no(trade_no);
                payStatusDto.setOut_trade_no(out_trade_no);
                payStatusDto.setTotal_amount(total_amount);
                payStatusDto.setApp_id(APP_ID);
                orderService.saveAliPayStatus(payStatusDto);
            }

            response.getWriter().write("success");

        }else{//verification failed
            response.getWriter().write("fail");
        }
    }
}
