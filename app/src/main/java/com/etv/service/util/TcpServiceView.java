package com.etv.service.util;

import com.etv.entity.BggImageEntity;

import java.util.List;

public interface TcpServiceView {

    void backSuccessBggImageInfo(List<BggImageEntity> lists, String errorDesc);

}
