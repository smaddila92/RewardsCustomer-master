@RunWith(SpringRunner.class)
@WebMvcTest(RewardsController.class)
public class RewardsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RewardsService rewardsService;

	@MockBean
	private CustomerRepository customerRepository;

	@Test
    public void testGetRewardsByCustomerId() throws Exception {
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "Customer1");

        when(customerRepository.findByCustomerId(customerId)).thenReturn(customer);

        Rewards expectedRewards = new Rewards(150, 100, 50);
        when(rewardsService.getRewardsByCustomerId(customerId)).thenReturn(expectedRewards);

        mockMvc.perform(get("/customers/{customerId}/rewards", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRewards").value(740))

        verify(customerRepository, times(1)).findByCustomerId(customerId);
        verify(rewardsService, times(1)).getRewardsByCustomerId(customerId);
    }

	@Test
	public void testGetRewardsByCustomerIdInvalidCustomerId() throws Exception {
		Long customerId = 1L;
		when(customerRepository.findByCustomerId(customerId)).thenReturn(null);

		mockMvc.perform(get("/customers/{customerId}/rewards", customerId)).andExpect(status().isBadRequest())
				.andExpect(content().string("Invalid / Missing customer Id "));

		verify(customerRepository, times(1)).findByCustomerId(customerId);
		verify(rewardsService, never()).getRewardsByCustomerId(anyLong());
	}
}
