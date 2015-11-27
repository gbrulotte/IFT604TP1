using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Web.Mvc;
using System.Web.Script.Serialization;
using HockeyWeb.Models;

namespace HockeyWeb.Controllers
{
    public class DataController : BaseController
    {
        private static ConcurrentDictionary<Guid, List<ParisViewModel>> Paris = new ConcurrentDictionary<Guid, List<ParisViewModel>>();
            
        [HttpPost]
        public ActionResult GetMatchList()
        {
            var udpClient = new UdpClient();
            dynamic result = null;
            try
            {
                udpClient.Connect("localhost", 8080);

                // Sends a message to the host to which you have connected.
                var sendBytes = Encoding.ASCII.GetBytes("ListerMatch");

                udpClient.Send(sendBytes, sendBytes.Length);

                //IPEndPoint object will allow us to read datagrams sent from any source.
                var remoteIpEndPoint = new IPEndPoint(IPAddress.Any, 0);

                // Blocks until a message returns on this socket from a remote host.
                var receiveBytes = udpClient.Receive(ref remoteIpEndPoint);
                var returnData = Encoding.ASCII.GetString(receiveBytes);

                // Uses the IPEndPoint object to determine which of these two hosts responded.
                Console.WriteLine("This is the message you received " +
                                             returnData);
                Console.WriteLine("This message was sent from " +
                                            remoteIpEndPoint.Address +
                                            " on their port number " +
                                            remoteIpEndPoint.Port);

                udpClient.Close();

                result = new JavaScriptSerializer().Deserialize<dynamic>(returnData);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
            return Json(result);
        }

        [HttpPost]
        public ActionResult GetMatchInfo(Guid matchId)
        {
            var udpClient = new UdpClient();
            dynamic result = null;
            try
            {
                udpClient.Connect("localhost", 8080);

                // Sends a message to the host to which you have connected.
                var sendBytes = Encoding.ASCII.GetBytes("MiseAJour~" + matchId);

                udpClient.Send(sendBytes, sendBytes.Length);

                //IPEndPoint object will allow us to read datagrams sent from any source.
                var remoteIpEndPoint = new IPEndPoint(IPAddress.Any, 0);

                // Blocks until a message returns on this socket from a remote host.
                var receiveBytes = udpClient.Receive(ref remoteIpEndPoint);
                var returnData = Encoding.ASCII.GetString(receiveBytes);

                // Uses the IPEndPoint object to determine which of these two hosts responded.
                Console.WriteLine("This is the message you received " +
                                             returnData);
                Console.WriteLine("This message was sent from " +
                                            remoteIpEndPoint.Address +
                                            " on their port number " +
                                            remoteIpEndPoint.Port);

                udpClient.Close();

                result = new JavaScriptSerializer().Deserialize<dynamic>(returnData);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
            return Json(result);
        }

        [HttpPost]
        public ActionResult BetOnMatch(Guid matchId, string team, int amount)
        {
            if (!Paris.ContainsKey(matchId))
            {
                Paris.TryAdd(matchId, new List<ParisViewModel>());
            }

            if (Paris[matchId].Count(x => x.UserId == UserId) != 0)
            {
                return Json(false);
            }

            var bet = new ParisViewModel
            {
                UserId = UserId,
                MatchId = matchId,
                Team = team,
                Amount = amount
            };

            Paris[matchId].Add(bet);
            bet.SendBet();

            return Json(true);
        }

        /// <summary>
        /// Get the ongoing bet for a match (Called after GetMatchDetails).
        /// </summary>
        /// <param name="matchId"></param>
        /// <returns></returns>
        [HttpPost]
        public ActionResult GetBet(Guid matchId)
        {
            List<ParisViewModel> parisList;
            ParisViewModel monParis;
            if (Paris.TryGetValue(matchId, out parisList) && (monParis = parisList.FirstOrDefault(x => x.UserId == UserId)) != null)
            {
                return Json(monParis);
            }

            return Json(new { });
        }

        [HttpPost]
        public ActionResult GetBetResults()
        {
            foreach (var pair in Paris.ToList())
            {
                var bet = pair.Value.FirstOrDefault(x => x.UserId == UserId);

                if (bet != null && bet.Result != null)
                {
                    var result = new
                    {
                        IsSuccess = true,
                        Payload = bet.Result
                    };

                    pair.Value.Remove(bet);

                    return Json(result);
                }
            }

            return Json(new
            {
                IsSuccess = false
            });
        }
    }
}
