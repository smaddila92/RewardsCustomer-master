import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.retailer.rewards.entity.Transaction;
import com.retailer.rewards.model.Rewards;
import com.retailer.rewards.repository.TransactionRepository;
import com.retailer.rewards.service.RewardsServiceImpl;

@SpringJUnitConfig
@SpringBootTest
public class RewardsServiceImplTest {

	@Mock
	TransactionRepository transactionRepository;

	@InjectMocks
	RewardsServiceImpl rewardsService;

	@Test
	public void testGetRewardsByCustomerId() {
		Long customerId = 1L;
		Timestamp now = Timestamp.from(Instant.now());
		Timestamp lastMonthTimestamp = rewardsService.getDateBasedOnOffSetDays(30);
		Timestamp lastSecondMonthTimestamp = rewardsService.getDateBasedOnOffSetDays(60);
		Timestamp lastThirdMonthTimestamp = rewardsService.getDateBasedOnOffSetDays(90);

		List<Transaction> lastMonthTransactions = new ArrayList<>();
		lastMonthTransactions.add(new Transaction(1L, customerId, 200.0, now));
		lastMonthTransactions.add(new Transaction(2L, customerId, 50.0, now));
		when(transactionRepository.findAllByCustomerIdAndTransactionDateBetween(customerId, lastMonthTimestamp, now))
				.thenReturn(lastMonthTransactions);

		List<Transaction> lastSecondMonthTransactions = new ArrayList<>();
		lastSecondMonthTransactions.add(new Transaction(3L, customerId, 100.0, now));
		when(transactionRepository.findAllByCustomerIdAndTransactionDateBetween(customerId, lastSecondMonthTimestamp,
				lastMonthTimestamp)).thenReturn(lastSecondMonthTransactions);

		List<Transaction> lastThirdMonthTransactions = new ArrayList<>();
		lastThirdMonthTransactions.add(new Transaction(4L, customerId, 300.0, now));
		when(transactionRepository.findAllByCustomerIdAndTransactionDateBetween(customerId, lastThirdMonthTimestamp,
				lastSecondMonthTimestamp)).thenReturn(lastThirdMonthTransactions);

		Rewards expectedRewards = new Rewards();
		expectedRewards.setCustomerId(customerId);
		expectedRewards.setLastMonthRewardPoints(0L + 50L);
		expectedRewards.setLastSecondMonthRewardPoints(100L);
		expectedRewards.setLastThirdMonthRewardPoints(200L + 50L);
		expectedRewards.setTotalRewards(0L + 50L + 100L + 200L + 50L);

		Rewards actualRewards = rewardsService.getRewardsByCustomerId(customerId);

		assertEquals(expectedRewards, actualRewards);
	}
}
