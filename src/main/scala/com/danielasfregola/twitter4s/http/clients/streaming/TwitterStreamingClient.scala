package com.danielasfregola.twitter4s.http.clients.streaming

import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.http.clients.{StreamingOAuthClient, OAuthClient}
import com.danielasfregola.twitter4s.http.clients.streaming.parameters.{StatusSampleParameters, StatusFilterParameters}
import com.danielasfregola.twitter4s.util.{ActorContextExtractor, Configurations}

import akka.actor.ActorRef


import scala.concurrent.Future

trait TwitterStreamingClient extends StreamingOAuthClient with Configurations with ActorContextExtractor {

  private val filterUrl = s"$streamingTwitterUrl/$twitterVersion/statuses/filter.json"
  private val sampleUrl = s"$streamingTwitterUrl/$twitterVersion/statuses/sample.json"

  /** Starts a streaming connection from Twitter's public API, filtered with the 'follow', 'track' and 'location' parameters.
    * Although all of those three params are optional, at least one must be specified.
    * The function only returns an empty future, that can be used to track failures in establishing the initial connection.
    * Since it's an asynchronous event stream, all the events will be parsed and forwarded to the [[akka.actor.Actor]] that invoked the original request, as entities of type `StreamingUpdate[StreamingEvent]`.
    * If the function wasn't invoked from an actor, you can specify which actor will receive the updates with the implicit variable `self`.
    * For more information see
    * <a href="https://dev.twitter.com/streaming/reference/post/statuses/filter" target="_blank">
    *   https://dev.twitter.com/streaming/reference/post/statuses/filter</a>.
    * Note: delimited is, for now, not supported
    *
    * @param follow : Optional, A comma separated list of user IDs, indicating the users to return statuses for in the stream.
    * @param track : Optional, Keywords to track. Phrases of keywords are specified by a comma-separated list.
    * @param locations : Optional, Specifies a set of bounding boxes to track.
    * @param stall_warnings : Specifies whether stall warnings (`WarningMessage`) should be delivered as part of the updates.
    */
  def getStatusesFilter(follow: Option[String] = None,
                        track: Option[String] = None,
                        locations: Option[String] = None,
                        stall_warnings: Boolean = false)(implicit self: ActorRef): Future[Unit] = {

    val parameters = StatusFilterParameters(follow, track, locations, stall_warnings)
    streamingPipeline(self, Get(filterUrl, parameters))
  }

  /** Same as getStatusesFilter, both GET and POST requests are supported, but GET requests with too many parameters may cause the request to be rejected for excessive URL length.
    * For more information see
    * <a href="https://dev.twitter.com/streaming/reference/post/statuses/filter" target="_blank">
    *   https://dev.twitter.com/streaming/reference/post/statuses/filter</a>.
    * Note: delimited is, for now, not supported
    *
    * @param follow : Optional, A comma separated list of user IDs, indicating the users to return statuses for in the stream.
    * @param track : Optional, Keywords to track. Phrases of keywords are specified by a comma-separated list.
    * @param locations : Optional, Specifies a set of bounding boxes to track.
    * @param stall_warnings : Specifies whether stall warnings (`WarningMessage`) should be delivered as part of the updates.
    */
  def postStatusesFilter(follow: Option[String] = None,
                        track: Option[String] = None,
                        locations: Option[String] = None,
                        stall_warnings: Boolean = false)(implicit self: ActorRef): Future[Unit] = {

    val parameters = StatusFilterParameters(follow, track, locations)
    streamingPipeline(self, Post(filterUrl, parameters.asInstanceOf[Product]))
  }

  /** Starts a streaming connection from Twitter's public API, which is a a small random sample of all public statuses.
    * The Tweets returned by the default access level are the same, so if two different clients connect to this endpoint, they will see the same Tweets.
    * The function only returns an empty future, that can be used to track failures in establishing the initial connection.
    * Since it's an asynchronous event stream, all the events will be parsed and forwarded to the [[akka.actor.Actor]] that invoked the original request, as entities of type `StreamingUpdate[StreamingEvent]`.
    * If the function wasn't invoked from an actor, you can specify which actor will receive the updates with the implicit variable `self`.
    * For more information see
    * <a href="https://dev.twitter.com/streaming/reference/get/statuses/sample" target="_blank">
    *   https://dev.twitter.com/streaming/reference/get/statuses/sample</a>.
    * Note: delimited is, for now, not supported
    *
    * @param stall_warnings : Specifies whether stall warnings (`WarningMessage`) should be delivered as part of the updates.
    */
  def getStatusesSample(stall_warnings: Boolean = false)(implicit self: ActorRef): Future[Unit] = {

    val parameters = StatusSampleParameters(stall_warnings)
    streamingPipeline(self, Get(sampleUrl, parameters))
  }
}
