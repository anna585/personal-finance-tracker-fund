package app.analytics.service;

import app.analytics.client.TransactionClient;
import app.analytics.dto.StatisticResponse;
import app.services.user.UserService;
import app.web.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final UserService userService;
    private final TransactionClient client;


    @PreAuthorize("hasRole('ADMIN')")
    public StatisticResponse getAllUsersForStatistic() {

        List<UserDto> userList = userService.getAllUsers();

        return  client.postStatisticForAllUsers(userList);
    }
}
