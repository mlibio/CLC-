package org.clc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.clc.entity.OperationLogs;
import org.clc.mapper.OperationLogsMapper;
import org.clc.service.OperationLogsService;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @description: TODO
 */
@Service
public class OperationLogsServiceImpl extends ServiceImpl<OperationLogsMapper, OperationLogs> implements OperationLogsService {
}
