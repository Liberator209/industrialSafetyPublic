/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Sensor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.abrain.IndustrialSafety.Employee.Employee;
import kr.co.abrain.IndustrialSafety.Employee.EmployeeRepository;
import kr.co.abrain.IndustrialSafety.Equipment.Equipment;
import kr.co.abrain.IndustrialSafety.Equipment.EquipmentRepository;
import kr.co.abrain.IndustrialSafety.fireBase.FcmService;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@Service
public class SensorService {
	@Data
	public class Counted{
		private String workerAddress;
		private Long count;
		
		Counted(){
		}
		
		Counted(String workerAddress, Long count){
			this.workerAddress = workerAddress;
			this.count = count;
		}
	}
	private SensorRepository sensorRepository;
	private EquipmentRepository equipmentRepository;
	private EmployeeRepository employeeRepository;
	private FcmService fcmService= new FcmService();
	
	SensorService(SensorRepository sensorRepository, EquipmentRepository equipmentRepository,
			EmployeeRepository employeeRepository){
		this.sensorRepository = sensorRepository;
		this.equipmentRepository = equipmentRepository;
		this.employeeRepository = employeeRepository;
	}
	/**
	 * 
	 * @param workerAddress
	 * @param workers
	 * if workerAddress String is in workers,
	 * return true or false
	 * @return is selected String workerAddress is in requested employee
	 */
	public boolean matchWorker(String workerAddress, List<Employee> workers) {
		for(Employee worker : workers) {
			if(worker.getDevice().equals(workerAddress))
				return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param workerAddress
	 * @param workers
	 * if workerAddress String is in workers,
	 * return worker or empty employee
	 * @return selected workerAddress as a employee class or empty class
	 */
	public Employee extractWorker(String workerAddress, List<Employee> workers) {
		for(Employee worker : workers) {
			if(worker.getDevice().equals(workerAddress))
				return worker;
		}
		return new Employee();
	}
	
	/**
	 * 
	 * @param sp
	 * extract workerAddress from list of workerIdentify,
	 * comes from sensor
	 * @return workerAddress from sensor as a string list
	 */
	public List<String> extractWorkerAddress(SensorParser sp) {
		List<String> ids = new ArrayList<>();
		sp.getWorkerIdentify().forEach(worker -> {
			ids.add(worker.getWorkerAddress());
		});
		return ids;
	}
	/**
	 * 
	 * @param sp
	 * check License among requested worker,
	 * if having license workers exists,
	 * return workers,
	 * or return none
	 * @return having license workers
	 */
	public Mono<List<Employee>> checkLicense(SensorParser sp){
		List<String> ids = extractWorkerAddress(sp);
		
		return
		Flux.fromIterable(ids).concatMap(id -> 
			this.employeeRepository.findByDevice(id).filter(worker->worker.isLicense()))
		.collectList();
		}
	
	/**
	 * 
	 * @param sp
	 * Look up last 10 minutes access log about requested device,
	 * return max access log workers,
	 * @return max access log workers
	 */
	public Mono<Counted> maxAccessLogs(SensorParser sp){
		List<String> ids = extractWorkerAddress(sp);
		
		return
		Flux.fromIterable(ids).concatMap(id ->
			this.sensorRepository.findByWorkerAddress(id)
			.filter(log -> log.getDeviceAddress().equals(sp.getDeviceAddress()))
			.filter(worker -> 
				Integer.parseInt(worker.getId().substring(worker.getId().length()-10, worker.getId().length())) > Integer.parseInt(worker.getId().substring(worker.getId().length()-10, worker.getId().length())) - 600)
				)
		.groupBy(sensor -> sensor.getWorkerAddress())
		.concatMap(groupedWorker -> groupedWorker.count()
				.map(count -> {
					return new Counted(groupedWorker.key(), count);
				}))
		.groupBy(Counted::getWorkerAddress)
		.concatMap(group -> group.reduce((o1, o2) -> o1.getCount() > o2.getCount() ? o1 : o2))
		.collectList()
		.map(lists -> lists.get(lists.size()-1))
		.switchIfEmpty(Mono.just(new Counted()));
		}
	
//	public Mono<Employee> lastOccupant(SensorParser sp){
//		
//		return
//			this.equipmentRepository.findById(sp.getDeviceAddress())
//			.map(equipment -> equipment.getLastOccupant())
//			.switchIfEmpty(Mono.just(new Employee()));
//	}
	
	/**
	 * @param sp
	 * extract employee class from sensor data
	 * @return received employee data
	 */
	public Mono<List<Employee>> employeeData(SensorParser sp){
		List<String> ids = extractWorkerAddress(sp);
		return
				Flux.fromIterable(ids).concatMap(id ->
				this.employeeRepository.findByDevice(id))
				.collectList();
	}
	/**
	 * @param sp
	 * extract equipment class from sensor data
	 * @return received equipment data
	 */
	public Mono<Equipment> equipmentData(SensorParser sp){
		return this.equipmentRepository.findByDeviceAddress(sp.getDeviceAddress());
	}
	
	/**
	 * @param sp
	 * find max Log accessor and check has license,
	 * type shouldn't be 2, because 2 is just d-zone (everyone is accessor)
	 * if has license, save him/her as occupant, send fcm message to occupant's mobile device
	 * others as accesor
	 * or return none
	 * @return recognized data or none, consistent by switchIf to maxRssiHasLicense
	 */
	public Mono<Object> maxLogAccessorHasLicense(SensorParser sp){
		return
				Mono.zip(maxAccessLogs(sp),checkLicense(sp),employeeData(sp),equipmentData(sp))
				.filter(data -> matchWorker(data.getT1().getWorkerAddress(), data.getT2()) && !sp.getType().equals("2"))
				.map(maxAccessorHasLicense -> {
					Sensor sensor = new Sensor();
					sensor.setDeviceAddress(sp.getDeviceAddress());
					sensor.setRssi(sp.findByWorkerAddress(maxAccessorHasLicense.getT1().getWorkerAddress()).getRssi());
					sensor.setTimeStamp(sp.findByWorkerAddress(maxAccessorHasLicense.getT1().getWorkerAddress()).getTimeStamp());
					sensor.setType(Integer.parseInt(sp.getType()) < 2 ? sp.getType() : "3");
					sensor.setWorkerAddress(maxAccessorHasLicense.getT1().getWorkerAddress());
					sensor.setId(maxAccessorHasLicense.getT1().getWorkerAddress() + sp.findByWorkerAddress(maxAccessorHasLicense.getT1().getWorkerAddress()).getTimeStamp());
					sensor.setWorker(extractWorker(maxAccessorHasLicense.getT1().getWorkerAddress(), maxAccessorHasLicense.getT3()));
					sensor.setDevice(maxAccessorHasLicense.getT4());
					
					maxAccessorHasLicense.getT4().setLastOccupant(extractWorker(maxAccessorHasLicense.getT1().getWorkerAddress(), maxAccessorHasLicense.getT2()));
					this.equipmentRepository.save(maxAccessorHasLicense.getT4()).subscribe();
					try {
						this.fcmService.sendMessage(extractWorker(maxAccessorHasLicense.getT1().getWorkerAddress(), maxAccessorHasLicense.getT2()), sp.getType().equals("0") ? "장비 점유 시작 안내" : "장비 사용 중단 안내" , sp.getDeviceAddress());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return extractOccupant(maxAccessorHasLicense.getT1().getWorkerAddress(), sp, maxAccessorHasLicense.getT3(),maxAccessorHasLicense.getT4())
							.map(s -> Flux.fromIterable(s).flatMap(x->x).collectList().zipWith(this.sensorRepository.save(sensor)));		
				});
	}
	/**
	 * @param sp
	 * come from maxLogAccessorHasLicens by switchIf,
	 * type shouldn't be 2, because 2 is just d-zone (everyone is accessor)
	 * find max rssi accessor and check has license,
	 * if has license, save him/her as occupant, send fcm message to occupant's mobile device
	 * others as accessor
	 * or return none
	 * @return  recognized data or none, consistent by switchIf to allAsAccessor
	 */
	public Mono<Object> maxRssiHasLicense(SensorParser sp){
		return
				Mono.zip(maxAccessLogs(sp),checkLicense(sp),employeeData(sp),equipmentData(sp))
				.filter(data -> data.getT3().get(0).isLicense() && !sp.getType().equals("2"))
				.map(data -> {
					Sensor sensor = new Sensor();
					sensor.setDeviceAddress(sp.getDeviceAddress());
					sensor.setRssi(sp.getWorkerIdentify().get(0).getRssi());
					sensor.setTimeStamp(sp.getWorkerIdentify().get(0).getTimeStamp());
					sensor.setType(sp.getType());
					sensor.setId(sp.getWorkerIdentify().get(0).getWorkerAddress() + sp.getWorkerIdentify().get(0).getTimeStamp());
					sensor.setWorker(data.getT3().get(0));
					sensor.setDevice(data.getT4());
					
					data.getT4().setLastOccupant(extractWorker(data.getT1().getWorkerAddress(), data.getT2()));
					this.equipmentRepository.save(data.getT4()).subscribe();
					try {
						this.fcmService.sendMessage(extractWorker(data.getT1().getWorkerAddress(), data.getT2()), sp.getType().equals("0") ? "장비 점유 시작 안내" : "장비 사용 중단 안내" , sp.getDeviceAddress());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return extractOccupant(sp.getWorkerIdentify().get(0).getWorkerAddress(), sp, data.getT3(), data.getT4())
							.map(s -> Flux.fromIterable(s).flatMap(x->x).collectList().zipWith(this.sensorRepository.save(sensor)));
				});
	}
	/**
	 * @param sp
	 * come from maxRssiHasLicense by switchIf,
	 * save all as accessor
	 * @return recognized data
	 */
	public Mono<Object> allAsAccessor(SensorParser sp){
		return
		Mono.zip(maxAccessLogs(sp),checkLicense(sp),employeeData(sp),equipmentData(sp))
		.map(everyOneIsAccessor -> {
			return extractOccupant("", sp, everyOneIsAccessor.getT3(), everyOneIsAccessor.getT4())
					.map(s -> Flux.fromIterable(s).flatMap(x->x).collectList().zipWith(Mono.just(new Sensor())));		
		});
	}
	
	/**
	 * @param sp
	 * maxLogAccessorHasLicense -> maxRssiHasLicense -> allAsAccessor
	 * @return recognized data
	 */
	public Mono<Object> workerRecognition(SensorParser sp) {
		return maxLogAccessorHasLicense(sp)
				.switchIfEmpty(
						maxRssiHasLicense(sp).switchIfEmpty(allAsAccessor(sp)));
	}
	
	/**
	 * 
	 * @param occupant
	 * @param sp
	 * @param workers
	 * @param device
	 * recognized occupant, accessor save to database, send fcm message to accessor's mobile device
	 * @return 
	 */
	public Mono<List<Mono<Sensor>>> extractOccupant(String occupant, SensorParser sp, List<Employee> workers, Equipment device){
		List<WorkerIdentify> accessors = new ArrayList<>();
		accessors = sp.getWorkerIdentify();
		
		return
		Flux.fromIterable(accessors)
		.filter(accessor -> !accessor.getWorkerAddress().equals(occupant))
		.map(accessor -> {
			Sensor sensor = new Sensor();
			sensor.setDeviceAddress(sp.getDeviceAddress());
			sensor.setRssi(accessor.getRssi());
			sensor.setTimeStamp(accessor.getTimeStamp());
			sensor.setType("3");
			sensor.setWorkerAddress(accessor.getWorkerAddress());
			sensor.setId(accessor.getWorkerAddress()+sp.findByWorkerAddress(accessor.getWorkerAddress()).getTimeStamp());
			sensor.setDevice(device);
			sensor.setWorker(extractWorker(accessor.getWorkerAddress(), workers));
			
			try {
				this.fcmService.sendMessage(extractWorker(accessor.getWorkerAddress(), workers), "위험구역 진입 안내" , sp.getDeviceAddress() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return this.sensorRepository.save(sensor);
		})
		.collectList();
		
	}
	
	
	}

