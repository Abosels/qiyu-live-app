package org.qiyu.live.living.provider.service;

import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.im.core.server.interfaces.dto.ImOfflineDTO;
import org.qiyu.live.im.core.server.interfaces.dto.ImOnlineDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.dto.PkStateDTO;

import java.util.List;

public interface ILivingRoomService {
    Integer startingLiving(LivingRoomReqDTO livingRoomReqDTO);

    boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 根据roomId查询直播间
     *
     * @param roomId
     * @return
     */
    LivingRoomRespDTO queryByRoomId(Integer roomId);

    /**
     * 直播间列表的分页查询
     * @param livingRoomReqDTO
     * @return
     */
    PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 从数据库中查询所有的正在直播的直播间
     * @param type
     * @return
     */
    List<LivingRoomRespDTO> listAllLivingRoomFromDB(Integer type);

    /**
     * 用户上线处理
     * @param imOnlineDTO
     */
    void userOnlineHandler(ImOnlineDTO imOnlineDTO);

    /**
     * 用户下线处理
     * @param imOfflineDTO
     */
    void userOfflineHandler(ImOfflineDTO imOfflineDTO);
    /**
     * 支持根据roomId查询出批量的userId(set)存储，3000个人，元素非常多，O(n);
     * @param livingRoomReqDTO
     * @return
     */
    List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO);

    boolean onlinePK(LivingRoomReqDTO livingRoomReqDTO);

    boolean offlinePK(LivingRoomReqDTO livingRoomReqDTO);
    /**
     * 根据roomID查询当前和你pk的对方userId是什么
     * @param roomId
     * @return
     */
    Long queryOnlinePkUserId(Integer roomId);

    /**
     * 根据 roomId 查询当前 PK 完整状态快照。
     *
     * @param roomId 直播间 ID
     * @return PkStateDTO，若不在 PK 中则返回 null
     */
    PkStateDTO queryCurrentPkState(Integer roomId);

    /**
     * 根据主播Id来查询直播间信息
     * @param anchorId
     * @return
     */
    LivingRoomRespDTO queryByAnchorId(Long anchorId);

}
