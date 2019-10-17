package com.mcoder.kft;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
@Getter
@AllArgsConstructor
public enum ChannelStatus {
    SUCCESS("1", true, LocalStatus.SUCCESS),
    FAIL("2", true, LocalStatus.FAIL),
    PROCESS("3", false, LocalStatus.PROCESS),
    EXCEPTION_SUCCESS("4", false, LocalStatus.PROCESS),
    EXCEPTION_FAIL("5", false, LocalStatus.PROCESS);

    private String status;
    private boolean isFinal;
    private String localStatus;

    public static ChannelStatus ofType(String status) {
        if (StringUtils.isBlank(status)) {
            return PROCESS;
        }
        return Arrays.asList(ChannelStatus.values()).stream()
                .filter(channelStatus -> channelStatus.getStatus().equals(status))
                .findFirst()
                .get();
    }
}
