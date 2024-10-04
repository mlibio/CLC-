package org.clc.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.clc.pojo.entity.OperationLogs;
import org.clc.server.mapper.OperationLogsMapper;
import org.clc.server.service.OperationLogsService;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @description: TODO
 */
@Service
public class OperationLogsServiceImpl extends ServiceImpl<OperationLogsMapper, OperationLogs> implements OperationLogsService {
}
