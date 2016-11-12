//package com.hui.service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//import java.util.concurrent.locks.ReentrantLock;
//
//public class ReentrantLockTest4 {
//
//	private ReentrantLock lock = new ReentrantLock();
//
//	public void tryLockTest() throws InterruptedException {
//
//		lock.tryLock(100, TimeUnit.MINUTES);
//
//		List<String>  tt = new ArrayList<>();
//		tt.notify();
//
//		ExecutorService service = Executors.newCachedThreadPool();
//
//		long beginTime = System.currentTimeMillis();
//		while(System.currentTimeMillis() - beginTime <100) {}
//		// 当前线程尝试获得锁，如果获得锁返回true，否则返回false
//		if(lock.tryLock()) {
//			try{
//				System.out.println(Thread.currentThread().getName() + " tryLock get lock");
//			} finally {
//				lock.unlock();
//				System.out.println(Thread.currentThread().getName() + " tryLock release lock");
//			}
//		} else {
//			System.out.println(Thread.currentThread().getName() + " tryLock can not get lock");
//		}
//
//	}
//
//
//	public void lockTest() {
//		try{
//			// 当前线程在锁可用时直接获得锁，锁不可用时阻塞当前线程
//			lock.lock();
//			System.out.println(Thread.currentThread().getName() + " lock get lock");
//			long beginTime = System.currentTimeMillis();
//			while(System.currentTimeMillis() - beginTime <1000) {}
//		} finally {
//			lock.unlock();
//			System.out.println(Thread.currentThread().getName() + " lock release lock");
//		}
//
//	}
//
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		final ReentrantLockTest4 test = new ReentrantLockTest4();
//		Thread tryLock = new Thread(new Runnable() {
//			public void run() {
//				try {
//					test.tryLockTest();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		},"tryLock_thread");
//
//		Thread lock = new Thread(new Runnable() {
//			public void run() {
//				test.lockTest();
//			}
//		},"lock_thread");
//
//		tryLock.start();
//		lock.start();
//	}
//
//}
//
////输出结果：
////lock_thread lock get lock
////tryLock_thread tryLock can not get lock
////lock_thread lock release lock