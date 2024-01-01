//package com.wyc.bgswitch.redis.list;
//
//import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
//import com.wyc.bgswitch.redis.constant.ListPrefix;
//
//import org.springframework.data.redis.core.ListOperations;
//
//import jakarta.annotation.Resource;
//
///**
// * @author wyc
// */
//public class CitadelActionListManager extends ListManager<CitadelGameAction> {
//
//
//    @Resource(name = "stringRedisTemplate")
//    private ListOperations<String, CitadelGameAction> listOps;
//
//
//    @Override
//    ListOperations<String, CitadelGameAction> getListOps() {
//        return listOps;
//    }
//
//    @Override
//    ListPrefix getPrefix() {
//        return ListPrefix.CITADEL_ACTIONS;
//    }
//}
