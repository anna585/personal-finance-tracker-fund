package app.analytics.service;

import app.analytics.client.TransactionClient;
import app.analytics.dto.StatisticResponse;
import app.services.user.UserService;
import app.web.dto.user.UsersDetailLists;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class StatisticService {

    private final UserService userService;
    private final TransactionClient client;

    @Cacheable(value = "statistic")
    @PreAuthorize("hasRole('ADMIN')")
    public StatisticResponse getAllUsersForStatistic() {

        UsersDetailLists usersDetailLists = userService.getAllUsersDetails();

        return  client.postStatisticForAllUsers(usersDetailLists);
    }
}
