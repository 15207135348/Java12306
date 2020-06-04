package com.yy.service.authority;

import com.yy.dao.repository.WxAccountRepository;
import com.yy.dao.entity.WxAccount;
import com.yy.integration.APIWeChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginWXService {


    @Autowired
    WxAccountRepository wxAccountRepository;

    public boolean login(String code) {
        //拿到微信用户的openID和secret等信息
        WxAccount newAccount = null;
        try {
            newAccount = APIWeChat.code2Session(code);
        } catch (Exception e) {
            return false;
        }
        if (newAccount == null) {
            return false;
        }
        WxAccount oldAccount = wxAccountRepository.findByOpenId(newAccount.getOpenId());
        if (oldAccount == null) {
            wxAccountRepository.save(newAccount);
        } else {
            oldAccount.setSessionKey(oldAccount.getSessionKey());
            oldAccount.setUnionId(oldAccount.getUnionId());
            wxAccountRepository.save(oldAccount);
        }
        return true;
    }

}
