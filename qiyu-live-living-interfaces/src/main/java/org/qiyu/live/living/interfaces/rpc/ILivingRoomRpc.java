package org.qiyu.live.living.interfaces.rpc;

import org.qiyu.live.common.interfaces.dto.PageWrapper;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.interfaces.dto.PkStateDTO;

import java.util.List;

public interface ILivingRoomRpc {
    /**
     * 开启直播间
     *
     * @param livingRoomReqDTO
     * @return
     */
    Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 在RPC层，除了roomID多导入一个userID，通过对比token的userId和传进来的userId 来一对一的关播而不是谁来都可以关
     *
     * @param livingRoomReqDTO
     * @return
     */
    boolean closeLivingRoom(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 根据用户id查询是否正在开播
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
     * 支持根据roomId查询出批量的userId(set)存储，3000个人，元素非常多，O(n);
     * @param livingRoomReqDTO
     * @return
     */
    List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 点击我要开播，有两个选项：1.默认直播间，2.pk直播间，此方法便是开始pk直播间。
     * @param livingRoomReqDTO
     * @return
     */
    boolean onlinePK(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 取消 PK 等待或退出 PK。
     *
     * <p>如果房间仍在等待池中则直接退出等待；
     * 如果已在 PK 进行中则需要走结算流程。</p>
     *
     * @param livingRoomReqDTO 包含 roomId 和服务端注入的 anchorId
     * @return true 取消成功；false 校验失败或已在 PK 中无法取消
     */
    boolean offlinePK(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 根据 roomId 查询当前 PK 对手的 userId。
     *
     * <p>注意：此方法为过渡方案，仅返回对手 userId。
     * 后续步骤会将其替换为 {@code queryCurrentPkState(Integer roomId)}
     * 返回完整 {@code PkStateDTO}，包含 pkId、双方比分、version、status 等。</p>
     *
     * @param roomId 当前直播间 ID
     * @return 对手主播的 userId，若当前不在 PK 中则返回 null
     */
    Long queryOnlinePkUserId(Integer roomId);

    /**
     * 根据 roomId 查询当前 PK 完整状态。
     *
     * <p>返回 {@link PkStateDTO} 包含 pkId、双方房间/主播、比分、
     * version、status 等，调用方可据此判断当前房间是否在 PK 中、
     * 属于 A/B 哪一方、PK 是否正在 RUNNING。</p>
     *
     * @param roomId 直播间 ID
     * @return 完整 PK 状态快照，若不在 PK 中则返回 null
     */
    PkStateDTO queryCurrentPkState(Integer roomId);

    /**
     * 根据主播Id来查询直播间信息
     * @param anchorId
     * @return
     */
    LivingRoomRespDTO queryByAnchorId(Long anchorId);

}
