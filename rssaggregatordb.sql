-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 05, 2017 at 03:37 PM
-- Server version: 5.7.14
-- PHP Version: 5.6.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `rssaggregatordb`
--

-- --------------------------------------------------------

--
-- Table structure for table `rss_domain`
--

CREATE TABLE `rss_domain` (
  `id` int(11) NOT NULL,
  `link` varchar(256) NOT NULL,
  `title` varchar(256) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=ucs2;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `login` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=ucs2;

-- --------------------------------------------------------

--
-- Table structure for table `user_domain_assoc`
--

CREATE TABLE `user_domain_assoc` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `rss_domain_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=ucs2;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `rss_domain`
--
ALTER TABLE `rss_domain`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_domain_assoc`
--
ALTER TABLE `user_domain_assoc`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `rss_domain`
--
ALTER TABLE `rss_domain`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `user_domain_assoc`
--
ALTER TABLE `user_domain_assoc`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
