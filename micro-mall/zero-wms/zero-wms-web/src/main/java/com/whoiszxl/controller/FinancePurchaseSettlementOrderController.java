package com.whoiszxl.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whoiszxl.bean.ResponseResult;
import com.whoiszxl.constant.PurchaseInboundOrderApproveResult;
import com.whoiszxl.constant.PurchaseInboundOrderStatus;
import com.whoiszxl.constant.PurchaseOrderStatus;
import com.whoiszxl.constants.PurchaseSettlementOrderStatus;
import com.whoiszxl.dto.PurchaseSettlementOrderDTO;
import com.whoiszxl.entity.PurchaseInboundOrder;
import com.whoiszxl.entity.PurchaseSettlementOrder;
import com.whoiszxl.entity.query.PurchaseSettlementOrderQuery;
import com.whoiszxl.entity.vo.PurchaseSettlementOrderVO;
import com.whoiszxl.feign.WmsFeignClient;
import com.whoiszxl.service.PurchaseInboundOrderService;
import com.whoiszxl.service.PurchaseOrderService;
import com.whoiszxl.service.PurchaseSettlementOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 财务中心的采购结算单表 前端控制器
 * </p>
 *
 * @author whoiszxl
 * @since 2021-07-21
 */
@Slf4j
@RestController
@RequestMapping("/finance/purchase-settlement-order")
@Api(tags = "采购结算单相关接口")
public class FinancePurchaseSettlementOrderController {


    @Autowired
    private PurchaseSettlementOrderService purchaseSettlementOrderService;

    @Autowired
    private PurchaseInboundOrderService purchaseInboundOrderService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping
    @ApiOperation(value = "分页查询采购入库单列表", notes = "分页查询采购入库单列表", response = PurchaseSettlementOrder.class)
    public ResponseResult<IPage<PurchaseSettlementOrder>> list(PurchaseSettlementOrderQuery query) {
        QueryWrapper<PurchaseSettlementOrder> wrapper = new QueryWrapper<>();
        if(query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }
        if(query.getSupplierId() != null) {
            wrapper.or().eq("supplier_id", query.getSupplierId());
        }
        IPage<PurchaseSettlementOrder> result = purchaseSettlementOrderService.page(new Page<>(query.getPage(), query.getSize()), wrapper);
        return ResponseResult.buildSuccess(result);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "通过主键ID查询采购入库单", notes = "通过主键ID查询采购入库单", response = PurchaseSettlementOrderVO.class)
    public ResponseResult<PurchaseSettlementOrderVO> getPurchaseSettlementOrderById(@PathVariable("id") Long id) {
        PurchaseSettlementOrderDTO purchaseSettlementOrderDTO = purchaseSettlementOrderService.getPurchaseSettlementOrderById(id);
        return ResponseResult.buildSuccess(purchaseSettlementOrderDTO.clone(PurchaseSettlementOrderVO.class));
    }

    @PutMapping
    @ApiOperation(value = "更新入库单", notes = "更新入库单", response = ResponseResult.class)
    public ResponseResult<Boolean> update(@RequestBody PurchaseSettlementOrderVO purchaseInboundOrderVO) {
        Boolean updateFlag = purchaseSettlementOrderService.updateSettlementOrder(purchaseInboundOrderVO);
        return ResponseResult.buildByFlag(updateFlag);
    }


    @PutMapping("/submit/approve/{id}")
    @ApiOperation(value = "提交采购入库单的审核", notes = "提交采购入库单的审核", response = ResponseResult.class)
    public ResponseResult submitOrderToApprove(@PathVariable("id") Long id) {
        //对采购入库单的订单状态进行校验
        PurchaseSettlementOrder purchaseSettlementOrder = purchaseSettlementOrderService.getById(id);
        if(purchaseSettlementOrder == null || !purchaseSettlementOrder.getStatus().equals(PurchaseSettlementOrderStatus.EDITING)) {
            return ResponseResult.buildError("采购结算单不存在或状态不为编辑中");
        }

        purchaseSettlementOrder.setStatus(PurchaseSettlementOrderStatus.WAIT_FOR_APPROVE);
        boolean updateFlag = purchaseSettlementOrderService.updateById(purchaseSettlementOrder);
        return ResponseResult.buildByFlag(updateFlag);
    }

    @PutMapping("/approve/{id}/{status}")
    @ApiOperation(value = "审核采购结算单", notes = "审核采购结算单", response = ResponseResult.class)
    public ResponseResult approve(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        //校验采购结算单状态
        PurchaseSettlementOrder purchaseSettlementOrder = purchaseSettlementOrderService.getById(id);
        if(purchaseSettlementOrder == null || !purchaseSettlementOrder.getStatus().equals(PurchaseSettlementOrderStatus.WAIT_FOR_APPROVE)) {
            return ResponseResult.buildError("采购结算单不存在或状态不为待审核中");
        }

        //进行审核状态更改
        purchaseSettlementOrderService.approve(id, status);

        //如果审核通过，需要发送给wms进行后续操作
        if(PurchaseInboundOrderApproveResult.PASSED.equals(status)) {
            PurchaseSettlementOrderDTO settlementOrderDTO = purchaseSettlementOrderService.getPurchaseSettlementOrderById(id);
            //wmsFeignClient.notifyFinishedPurchaseSettlementOrderEvent(settlementOrderDTO.getPurchaseInboundOrderId());

            //1. 更新采购入库单状态为已完成
            purchaseInboundOrderService.updateStatus(settlementOrderDTO.getPurchaseInboundOrderId(), PurchaseInboundOrderStatus.FINISHED);

            //2. 更新采购单状态为已完成
            PurchaseInboundOrder purchaseInboundOrder = purchaseInboundOrderService.getById(settlementOrderDTO.getPurchaseInboundOrderId());
            purchaseOrderService.updateStatus(purchaseInboundOrder.getPurchaseOrderId(), PurchaseOrderStatus.FINISHED);
        }
        return ResponseResult.buildSuccess();
    }

}

