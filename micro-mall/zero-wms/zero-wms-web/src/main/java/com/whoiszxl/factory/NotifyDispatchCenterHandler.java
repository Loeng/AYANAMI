package com.whoiszxl.factory;

import com.whoiszxl.client.DispatchClient;
import com.whoiszxl.dto.PurchaseInboundOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotifyDispatchCenterHandler extends AbstractPurchaseInboundOrderHandler {

    @Autowired
    private DispatchClient dispatchClient;

    @Override
    protected PurchaseInboundOrderResult doExecute(PurchaseInboundOrderDTO purchaseInboundOrderDTO) {
        dispatchClient.notifyPurchaseInboundFinished(purchaseInboundOrderDTO);
        return new PurchaseInboundOrderResult(true);
    }
}